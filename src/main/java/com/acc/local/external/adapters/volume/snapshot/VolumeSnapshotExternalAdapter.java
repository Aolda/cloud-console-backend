package com.acc.local.external.adapters.volume.snapshot;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import com.acc.local.external.dto.cinder.snapshot.CreateSnapshotRequest;
import com.acc.local.external.modules.cinder.CinderSnapshotsModule;
import com.acc.local.external.ports.VolumeSnapshotExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class VolumeSnapshotExternalAdapter implements VolumeSnapshotExternalPort {
    private final CinderSnapshotsModule cinderSnapshotsModule;

    @Override
    public PageResponse<VolumeSnapshotResponse> callListSnapshots(String token, String projectId, String marker, int limit) {

        Map<String, String> queryParams = new HashMap<>();
        int requestLimit = (limit > 0) ? limit + 1 : 0;

        if (requestLimit > 0) {
            queryParams.put("limit", String.valueOf(requestLimit));
        }
        if (marker != null && !marker.isEmpty()) {
            queryParams.put("marker", marker);
        }
        try {
            ResponseEntity<JsonNode> response = cinderSnapshotsModule.listSnapshots(token, projectId, queryParams);

            JsonNode body = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() || body == null || body.path("snapshots").isMissingNode()) {
                throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE);
            }

            JsonNode snapshotsNode = body.get("snapshots");
            List<VolumeSnapshotResponse> contents = StreamSupport.stream(snapshotsNode.spliterator(), false)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return getSnapshotsPageResponse(marker, limit, contents);

        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.NOT_FOUND) {
                throw new VolumeException(VolumeErrorCode.SNAPSHOT_NOT_FOUND, e);
            } else if (status == HttpStatus.FORBIDDEN) {
                throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS, e);
            }
            throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
        }
    }
    @Override
    public VolumeSnapshotResponse callGetSnapshotDetails(String token, String projectId, String snapshotId) {
        try {
            ResponseEntity<JsonNode> response = cinderSnapshotsModule.getSnapshot(token, projectId, snapshotId);

            JsonNode body = response.getBody();
            if (body == null || body.path("snapshot").isMissingNode()) {
                throw new VolumeException(VolumeErrorCode.SNAPSHOT_NOT_FOUND);
            }

            JsonNode snapshotNode = body.get("snapshot");
            return convertToDto(snapshotNode);

        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.NOT_FOUND) {
                throw new VolumeException(VolumeErrorCode.SNAPSHOT_NOT_FOUND, e);
            } else if (status == HttpStatus.FORBIDDEN) {
                throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS, e);
            }
            throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
        }
    }
    @Override
    public ResponseEntity<Void> callDeleteSnapshot(String token, String projectId, String snapshotId) {

        try {
            ResponseEntity<JsonNode> response = cinderSnapshotsModule.deleteSnapshot(token, projectId, snapshotId);
            return ResponseEntity.status(response.getStatusCode()).build();

        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.ACCEPTED || status == HttpStatus.NO_CONTENT) {
                return ResponseEntity.status(status).build();
            }
            if (status == HttpStatus.NOT_FOUND) {
                throw new VolumeException(VolumeErrorCode.SNAPSHOT_NOT_FOUND, e);
            } else if (status == HttpStatus.CONFLICT) {
                throw new VolumeException(VolumeErrorCode.SNAPSHOT_IN_USE, e);
            } else if (status == HttpStatus.FORBIDDEN) {
                throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS, e);
            }
            throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
        }
    }

    @Override
    public VolumeSnapshotResponse callCreateSnapshot(String token, String projectId, VolumeSnapshotRequest request) {

        CreateSnapshotRequest.Snapshot snapshotData = CreateSnapshotRequest.Snapshot.builder()
                .volumeId(request.getSourceVolumeId())
                .name(request.getName())
                .build();

        CreateSnapshotRequest cinderRequest = CreateSnapshotRequest.builder()
                .snapshot(snapshotData)
                .build();

        try {
            ResponseEntity<JsonNode> response = cinderSnapshotsModule.createSnapshot(token, projectId, cinderRequest);
            JsonNode body = response.getBody();
            if (body == null || body.path("snapshot").isMissingNode()) {
                throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE);
            }

            return convertToDto(body.get("snapshot"));

        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            String errorBody = e.getResponseBodyAsString();
            if (status == HttpStatus.BAD_REQUEST) {
                if (errorBody != null && (errorBody.contains("in-use") || errorBody.contains("Invalid volume state"))) {
                    throw new VolumeException(VolumeErrorCode.SNAPSHOT_IN_USE, e);
                }
                throw new VolumeException(VolumeErrorCode.INVALID_REQUEST_PARAMETER, e);
            } else if (status == HttpStatus.CONFLICT) {
                throw new VolumeException(VolumeErrorCode.SNAPSHOT_IN_USE, e);
            } else if (status == HttpStatus.FORBIDDEN) {
                throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS, e);
            }
            throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
        }
    }

    private PageResponse<VolumeSnapshotResponse> getSnapshotsPageResponse(String marker, int limit, List<VolumeSnapshotResponse> contents) {
        boolean hasNext = (limit > 0) && (contents.size() > limit);
        if (hasNext) {
            contents.remove(limit);
        }

        boolean isFirst = (marker == null || marker.isEmpty());
        String nextMarker = (hasNext && !contents.isEmpty())
                ? contents.get(contents.size() - 1).getSnapshotId()
                : null;

        return PageResponse.<VolumeSnapshotResponse>builder()
                .contents(contents)
                .size(contents.size())
                .first(isFirst)
                .last(!hasNext)
                .nextMarker(nextMarker)
                .prevMarker(marker)
                .build();
    }

    private VolumeSnapshotResponse convertToDto(JsonNode snapshotNode) {
        return VolumeSnapshotResponse.builder()
                .snapshotId(snapshotNode.path("id").asText())
                .name(snapshotNode.path("name").asText(null))
                .createdAt(parseDate(snapshotNode.path("created_at").asText(null)))
                .sourceVolumeId(snapshotNode.path("volume_id").asText(null)) // path()와 asText(null) 사용
                .status(snapshotNode.path("status").asText(null))
                .sizeGb(snapshotNode.path("size").asInt(0)) // path()와 asInt(0) 사용
                .build();
    }

    private LocalDateTime parseDate(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            LocalDateTime utcDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            ZonedDateTime utcZoned = utcDateTime.atZone(ZoneId.of("UTC"));
            return utcZoned.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }
}
