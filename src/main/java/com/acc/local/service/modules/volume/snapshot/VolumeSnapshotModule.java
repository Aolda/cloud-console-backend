package com.acc.local.service.modules.volume.snapshot;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotsResponse.VolumeSnapshot;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotsResponse;
import com.acc.local.external.modules.cinder.CinderSnapshotsModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VolumeSnapshotModule {
    private final CinderSnapshotsModule cinderSnapshotsModule;

    public VolumeSnapshotsResponse getSnapshots(String token) {

        ResponseEntity<JsonNode> response = cinderSnapshotsModule.listSnapshots(token, Collections.emptyMap());

        JsonNode body = response.getBody();
        if(body == null || !body.has("snapshots")) {
            return VolumeSnapshotsResponse.builder()
                    .VolumeSnapshots(List.of())
                    .build();
        }

        List<VolumeSnapshot> snapshots =
                body.get("snapshots").findValuesAsText("id") == null ? List.of() :
                        body.get("snapshots").elements().hasNext() ?
                                convertToDtoList(body.get("snapshots")) : List.of();

        return VolumeSnapshotsResponse.builder()
                .VolumeSnapshots(snapshots)
                .build();
    }
    private List<VolumeSnapshot> convertToDtoList(JsonNode snapshotsNode) {
        return snapshotsNode.findValuesAsText("id") == null ? List.of() :
                snapshotsNode.findParents("id").stream().map(snapshotNode -> VolumeSnapshot.builder()
                        .id(snapshotNode.get("id").asText())
                        .name(snapshotNode.hasNonNull("name") ? snapshotNode.get("name").asText() : null)
                        .createdAt(parseDate(snapshotNode.path("created_at").asText(null)))
                        .volumeId(snapshotNode.hasNonNull("volume_id") ? snapshotNode.get("volume_id").asText() : null)
                        .status(snapshotNode.hasNonNull("status") ? snapshotNode.get("status").asText() : null)
                        .size(snapshotNode.hasNonNull("size") ? snapshotNode.get("size").asInt() : 0)
                        .build()
                ).collect(Collectors.toList());
    }

    private LocalDateTime parseDate(String dateTimeStr) {
        if (dateTimeStr == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

        LocalDateTime utcDateTime = LocalDateTime.parse(dateTimeStr, formatter);
        ZonedDateTime utcZoned = utcDateTime.atZone(ZoneId.of("UTC"));
        return utcZoned.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    public ResponseEntity<Void> deleteSnapshot(String token, String snapshotId) {
        try {
            ResponseEntity<JsonNode> response = cinderSnapshotsModule.deleteSnapshot(token, snapshotId);

            return ResponseEntity.status(response.getStatusCode()).build();

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.ACCEPTED || e.getStatusCode() == HttpStatus.NO_CONTENT) {
                return ResponseEntity.status(e.getStatusCode()).build();
            }

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new VolumeException(VolumeErrorCode.SNAPSHOT_NOT_FOUND);
            } else if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new VolumeException(VolumeErrorCode.SNAPSHOT_IN_USE);
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS);
            }

            throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
        }
    }




}
