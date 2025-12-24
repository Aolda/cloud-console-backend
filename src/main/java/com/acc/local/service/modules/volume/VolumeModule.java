package com.acc.local.service.modules.volume;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import com.acc.local.external.ports.VolumeExternalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VolumeModule {
    private final VolumeExternalPort volumeExternalPort;

    public PageResponse<VolumeResponse> getVolumes(PageRequest page, String projectId, String token) {

        return volumeExternalPort.callListVolumes(
                token,
                projectId,
                page.getMarker(),
                (page.getLimit() != null) ? page.getLimit() : 10
        );
    }

    public VolumeResponse getVolumeDetails(String token, String projectId, String volumeId) {
        return volumeExternalPort.callGetVolumeDetails(token, projectId, volumeId);
    }

    public ResponseEntity<Void> deleteVolume(String token, String projectId, String volumeId) {
        return volumeExternalPort.callDeleteVolume(token, projectId, volumeId);
    }

    public VolumeResponse createVolume(String token, String projectId, VolumeRequest request) {
        return volumeExternalPort.callCreateVolume(token, projectId, request);
    }
}