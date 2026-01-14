package com.acc.local.external.adapters.volume;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import com.acc.local.external.dto.cinder.volume.CreateVolumeRequest;
import com.acc.local.external.dto.cinder.response.CinderVolumesResponse;
import com.acc.local.external.dto.cinder.response.CinderVolumeResponse;
import com.acc.local.external.modules.cinder.CinderVolumesModule;
import com.acc.local.external.ports.VolumeExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            ResponseEntity<CinderVolumesResponse> response = cinderVolumesModule.listVolumes(token, projectId, queryParams);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE);
            }

            List<VolumeResponse> contents = response.getBody().getVolumes().stream()
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
            ResponseEntity<CinderVolumeResponse> response = cinderVolumesModule.getVolume(token, projectId, volumeId);

            if (response.getBody() == null || response.getBody().getVolume() == null) {
                throw new VolumeException(VolumeErrorCode.VOLUME_NOT_FOUND);
            }

            return convertToDto(response.getBody().getVolume());

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

    private VolumeResponse convertToDto(CinderVolumesResponse.Volume volume) {
        return VolumeResponse.builder()
                .volumeId(volume.getId())
                .name(volume.getName())
                .size(volume.getSize())
                .status(volume.getStatus())
                .volumeType(volume.getVolumeType())
                .description(volume.getDescription())
                .availabilityZone(volume.getAvailabilityZone())
                .createdAt(volume.getCreatedAt())
                .bootable(volume.getBootable() != null ? volume.getBootable().toString() : null)
                .build();
    }

    // Overload for POST create (kept as JsonNode by design per requirements)
    private VolumeResponse convertToDto(JsonNode volumeNode) {
        if (volumeNode == null || volumeNode.isNull()) {
            throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE);
        }
        Boolean bootable = null;
        JsonNode bootableNode = volumeNode.get("bootable");
        if (bootableNode != null && !bootableNode.isNull()) {
            String b = bootableNode.asText();
            if ("true".equalsIgnoreCase(b)) bootable = true;
            else if ("false".equalsIgnoreCase(b)) bootable = false;
        }
        return VolumeResponse.builder()
                .volumeId(s(volumeNode, "id"))
                .name(s(volumeNode, "name"))
                .size(i(volumeNode, "size"))
                .status(s(volumeNode, "status"))
                .volumeType(s(volumeNode, "volume_type"))
                .description(s(volumeNode, "description"))
                .availabilityZone(s(volumeNode, "availability_zone"))
                .createdAt(s(volumeNode, "created_at"))
                .bootable(bootable != null ? bootable.toString() : null)
                .build();
    }

    private String s(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }
    private Integer i(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asInt();
    }
}
