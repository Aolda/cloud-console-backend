package com.acc.local.service.adapters.volume;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import com.acc.local.service.modules.volume.VolumeModule;
import com.acc.local.service.modules.volume.VolumeUtil;
import com.acc.local.service.ports.VolumeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class VolumeServiceAdapter implements VolumeServicePort {

    private final VolumeModule volumeModule;
    private final VolumeUtil volumeUtil;

    @Override
    public PageResponse<VolumeResponse> getVolumes(PageRequest page, String projectId, String keystoneToken) {
        return volumeModule.getVolumes(page, projectId, keystoneToken);
    }

    @Override
    public VolumeResponse getVolumeDetails(String projectId, String keystoneToken, String volumeId) {
        if (!volumeUtil.validateVolumeId(volumeId)) {
            throw new VolumeException(VolumeErrorCode.INVALID_VOLUME_ID);
        }
        return volumeModule.getVolumeDetails(keystoneToken, projectId, volumeId);
    }

    @Override
    public ResponseEntity<Void> deleteVolume(String projectId, String keystoneToken, String volumeId) {
        if (!volumeUtil.validateVolumeId(volumeId)) {
            throw new VolumeException(VolumeErrorCode.INVALID_VOLUME_ID);
        }
        return volumeModule.deleteVolume(keystoneToken, projectId, volumeId);
    }

    @Override
    public VolumeResponse createVolume(String projectId, String keystoneToken, VolumeRequest request) {
        if (!volumeUtil.validateVolumeSize(request.getSize())) {
            throw new VolumeException(VolumeErrorCode.INVALID_VOLUME_SIZE);
        }
        if (!volumeUtil.validateVolumeName(request.getName())) {
            throw new VolumeException(VolumeErrorCode.INVALID_VOLUME_NAME);
        }
        return volumeModule.createVolume(keystoneToken, projectId, request);
    }
}