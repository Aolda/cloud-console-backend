package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import org.springframework.http.ResponseEntity;

public interface VolumeExternalPort {
    PageResponse<VolumeResponse> callListVolumes(String token, String projectId, String marker, int limit);

    VolumeResponse callGetVolumeDetails(String token, String projectId, String volumeId);

    ResponseEntity<Void> callDeleteVolume(String token, String projectId, String volumeId);

    VolumeResponse callCreateVolume(String token, String projectId, VolumeRequest request);
}
