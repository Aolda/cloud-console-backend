package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import org.springframework.http.ResponseEntity;

public interface VolumeServicePort {
    PageResponse<VolumeResponse> getVolumes(PageRequest page, String userId, String projectId);

    VolumeResponse getVolumeDetails(String userId, String projectId, String volumeId);

    ResponseEntity<Void> deleteVolume(String userId, String projectId, String volumeId);

    VolumeResponse createVolume(String userId, String projectId, VolumeRequest request);
}
