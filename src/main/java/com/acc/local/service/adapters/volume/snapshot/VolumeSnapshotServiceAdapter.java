package com.acc.local.service.adapters.volume.snapshot;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import com.acc.local.external.dto.cinder.snapshot.CreateSnapshotRequest;
import com.acc.local.service.modules.volume.snapshot.VolumeSnapshotModule;
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

    @Override
    public PageResponse<VolumeSnapshotResponse> getSnapshots(PageRequest page, String token){
        return volumeSnapshotModule.getSnapshots(page,"project-id",token);
    }
    @Override
    public VolumeSnapshotResponse getSnapshotDetails(String token, String snapshotId) {
        return volumeSnapshotModule.getSnapshotDetails(token,"project-id", snapshotId);
    }

    @Override
    public ResponseEntity<Void> deleteSnapshot(String token, String snapshotId) {
        return volumeSnapshotModule.deleteSnapshot(token, "project-id",snapshotId);
    }
    @Override
    public VolumeSnapshotResponse createSnapshot(String token, VolumeSnapshotRequest request){
        return volumeSnapshotModule.createSnapshot(token, "project-id",request);
    }
}
