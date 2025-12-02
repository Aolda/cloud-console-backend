package com.acc.local.external.adapters.volume;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import com.acc.local.external.dto.cinder.volume.CreateVolumeRequest;
import com.acc.local.external.modules.cinder.CinderVolumesModule;
import com.acc.local.external.ports.VolumeExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class VolumeExternalAdapter implements VolumeExternalPort {
    private final CinderVolumesModule cinderVolumesModule;

    @Override
    public PageResponse<VolumeResponse> callListVolumes(String token, String projectId, String marker, int limit) {

        Map<String, String> queryParams = new HashMap<>();
        int requestLimit = (limit > 0) ? limit + 1 : 0;

        if (requestLimit > 0) {
            queryParams.put("limit", String.valueOf(requestLimit));
        }
        if (marker != null && !marker.isEmpty()) {
            queryParams.put("marker", marker);
        }
        try {
            ResponseEntity<JsonNode> response = cinderVolumesModule.listVolumes(token, projectId, queryParams);

            JsonNode body = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() || body == null || body.path("volumes").isMissingNode()) {
                throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE);
            }

            JsonNode volumesNode = body.get("volumes");
            List<VolumeResponse> contents = StreamSupport.stream(volumesNode.spliterator(), false)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return getVolumesPageResponse(marker, limit, contents);

        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.NOT_FOUND) {
                throw new VolumeException(VolumeErrorCode.VOLUME_NOT_FOUND, e);
            } else if (status == HttpStatus.FORBIDDEN) {
                throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS, e);
            }
            throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
        }
    }

    @Override
    public VolumeResponse callGetVolumeDetails(String token, String projectId, String volumeId) {
        try {
            ResponseEntity<JsonNode> response = cinderVolumesModule.getVolume(token, projectId, volumeId);

            JsonNode body = response.getBody();
            if (body == null || body.path("volume").isMissingNode()) {
                throw new VolumeException(VolumeErrorCode.VOLUME_NOT_FOUND);
            }

            JsonNode volumeNode = body.get("volume");
            return convertToDto(volumeNode);

        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.NOT_FOUND) {
                throw new VolumeException(VolumeErrorCode.VOLUME_NOT_FOUND, e);
            } else if (status == HttpStatus.FORBIDDEN) {
                throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS, e);
            }
            throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
        }
    }

    @Override
    public ResponseEntity<Void> callDeleteVolume(String token, String projectId, String volumeId) {

        try {
            ResponseEntity<JsonNode> response = cinderVolumesModule.deleteVolume(token, projectId, volumeId);
            return ResponseEntity.status(response.getStatusCode()).build();

        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.ACCEPTED || status == HttpStatus.NO_CONTENT) {
                return ResponseEntity.status(status).build();
            }
            if (status == HttpStatus.NOT_FOUND) {
                throw new VolumeException(VolumeErrorCode.VOLUME_NOT_FOUND, e);
            } else if (status == HttpStatus.CONFLICT) {
                throw new VolumeException(VolumeErrorCode.VOLUME_IN_USE, e);
            } else if (status == HttpStatus.FORBIDDEN) {
                throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS, e);
            }
            throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
        }
    }

    @Override
    public VolumeResponse callCreateVolume(String token, String projectId, VolumeRequest request) {

        CreateVolumeRequest.Volume volumeData = CreateVolumeRequest.Volume.builder()
                .size(request.getSize())
                .name(request.getName())
                .description(request.getDescription())
                .volumeType(request.getVolumeType())
                .build();

        CreateVolumeRequest cinderRequest = CreateVolumeRequest.builder()
                .volume(volumeData)
                .build();

        try {
            ResponseEntity<JsonNode> response = cinderVolumesModule.createVolume(token, projectId, cinderRequest);
            JsonNode body = response.getBody();
            if (body == null || body.path("volume").isMissingNode()) {
                throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE);
            }

            return convertToDto(body.get("volume"));

        } catch (WebClientResponseException e) {
            HttpStatusCode status = e.getStatusCode();
            String errorBody = e.getResponseBodyAsString();
            if (status == HttpStatus.BAD_REQUEST) {
                if (errorBody != null && (errorBody.contains("quota") || errorBody.contains("exceeded"))) {
                    throw new VolumeException(VolumeErrorCode.VOLUME_QUOTA_EXCEEDED, e);
                }
                throw new VolumeException(VolumeErrorCode.INVALID_REQUEST_PARAMETER, e);
            } else if (status == HttpStatus.CONFLICT) {
                throw new VolumeException(VolumeErrorCode.VOLUME_IN_USE, e);
            } else if (status == HttpStatus.FORBIDDEN) {
                throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS, e);
            }
            throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
        }
    }

    private PageResponse<VolumeResponse> getVolumesPageResponse(String marker, int limit, List<VolumeResponse> contents) {
        boolean hasNext = (limit > 0) && (contents.size() > limit);
        if (hasNext) {
            contents.remove(limit);
        }

        boolean isFirst = (marker == null || marker.isEmpty());
        String nextMarker = (hasNext && !contents.isEmpty())
                ? contents.get(contents.size() - 1).getVolumeId()
                : null;

        return PageResponse.<VolumeResponse>builder()
                .contents(contents)
                .size(contents.size())
                .first(isFirst)
                .last(!hasNext)
                .nextMarker(nextMarker)
                .prevMarker(marker)
                .build();
    }

    private VolumeResponse convertToDto(JsonNode volumeNode) {
        return VolumeResponse.builder()
                .volumeId(volumeNode.path("id").asText())
                .name(volumeNode.path("name").asText(null))
                .size(volumeNode.path("size").asInt(0))
                .status(volumeNode.path("status").asText(null))
                .volumeType(volumeNode.path("volume_type").asText(null))
                .description(volumeNode.path("description").asText(null))
                .availabilityZone(volumeNode.path("availability_zone").asText(null))
                .createdAt(volumeNode.path("created_at").asText(null))
                .bootable(volumeNode.path("bootable").asText(null))
                .build();
    }
}
