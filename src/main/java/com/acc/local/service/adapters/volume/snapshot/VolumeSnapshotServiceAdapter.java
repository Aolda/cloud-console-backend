package com.acc.local.service.adapters.volume.snapshot;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import com.acc.local.service.modules.volume.snapshot.VolumeSnapshotModule;
import com.acc.local.service.modules.volume.snapshot.VolumeSnapshotUtil;
import com.acc.local.service.ports.VolumeSnapshotServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class VolumeSnapshotServiceAdapter implements VolumeSnapshotServicePort {

    private final VolumeSnapshotModule volumeSnapshotModule;
    private final VolumeSnapshotUtil volumeSnapshotUtil;

    @Override
    public PageResponse<VolumeSnapshotResponse> getSnapshots(PageRequest page, String projectId, String keystoneToken){
        return volumeSnapshotModule.getSnapshots(page, projectId, keystoneToken);
    }
    @Override
    public VolumeSnapshotResponse getSnapshotDetails(String projectId, String keystoneToken, String snapshotId) {
        if (!volumeSnapshotUtil.validateSnapshotId(snapshotId)) {
            throw new VolumeException(VolumeErrorCode.INVALID_SNAPSHOT_ID);
        }
        return volumeSnapshotModule.getSnapshotDetails(keystoneToken, projectId, snapshotId);
    }

    @Override
    public ResponseEntity<Void> deleteSnapshot(String projectId, String keystoneToken, String snapshotId) {
        if (!volumeSnapshotUtil.validateSnapshotId(snapshotId)) {
            throw new VolumeException(VolumeErrorCode.INVALID_SNAPSHOT_ID);
        }
        return volumeSnapshotModule.deleteSnapshot(keystoneToken, projectId, snapshotId);
    }
    @Override
    public VolumeSnapshotResponse createSnapshot(String projectId, String keystoneToken, VolumeSnapshotRequest request){
        if (!volumeSnapshotUtil.validateVolumeId(request.getSourceVolumeId())) {
            throw new VolumeException(VolumeErrorCode.INVALID_VOLUME_ID);
        }
        if (!volumeSnapshotUtil.validateSnapshotName(request.getName())) {
            throw new VolumeException(VolumeErrorCode.INVALID_SNAPSHOT_NAME);
        }
        return volumeSnapshotModule.createSnapshot(keystoneToken, projectId, request);
    }
}
