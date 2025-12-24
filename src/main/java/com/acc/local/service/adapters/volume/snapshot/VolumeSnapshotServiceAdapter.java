package com.acc.local.service.adapters.volume.snapshot;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import com.acc.local.service.modules.auth.AuthModule;
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
    private final AuthModule authModule;

    @Override
    public PageResponse<VolumeSnapshotResponse> getSnapshots(String userId,String projectId,PageRequest page){
        String keystoneToken = authModule.issueProjectScopeToken(userId, projectId);
        return volumeSnapshotModule.getSnapshots(keystoneToken, projectId, page);
    }
    @Override
    public VolumeSnapshotResponse getSnapshotDetails(String userId, String projectId, String snapshotId) {
        if (!volumeSnapshotUtil.validateSnapshotId(snapshotId)) {
            throw new VolumeException(VolumeErrorCode.INVALID_SNAPSHOT_ID);
        }
        String keystoneToken = authModule.issueProjectScopeToken(userId, projectId);
        return volumeSnapshotModule.getSnapshotDetails(keystoneToken, projectId, snapshotId);
    }

    @Override
    public ResponseEntity<Void> deleteSnapshot(String userId, String projectId, String snapshotId) {
        if (!volumeSnapshotUtil.validateSnapshotId(snapshotId)) {
            throw new VolumeException(VolumeErrorCode.INVALID_SNAPSHOT_ID);
        }
        String keystoneToken = authModule.issueProjectScopeToken(userId, projectId);
        return volumeSnapshotModule.deleteSnapshot(keystoneToken, projectId, snapshotId);
    }
    @Override
    public VolumeSnapshotResponse createSnapshot(String userId, String projectId, VolumeSnapshotRequest request){
        if (!volumeSnapshotUtil.validateVolumeId(request.getSourceVolumeId())) {
            throw new VolumeException(VolumeErrorCode.INVALID_VOLUME_ID);
        }
        if (!volumeSnapshotUtil.validateSnapshotName(request.getName())) {
            throw new VolumeException(VolumeErrorCode.INVALID_SNAPSHOT_NAME);
        }
        String keystoneToken = authModule.issueProjectScopeToken(userId, projectId);
        return volumeSnapshotModule.createSnapshot(keystoneToken, projectId,request);
    }
}
