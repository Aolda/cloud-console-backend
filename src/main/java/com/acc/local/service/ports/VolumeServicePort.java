package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import org.springframework.http.ResponseEntity;

public interface VolumeServicePort {
    PageResponse<VolumeResponse> getVolumes(PageRequest page, String token);

    VolumeResponse getVolumeDetails(String token, String volumeId);

    ResponseEntity<Void> deleteVolume(String token, String volumeId);

    VolumeResponse createVolume(String token, VolumeRequest request);
}