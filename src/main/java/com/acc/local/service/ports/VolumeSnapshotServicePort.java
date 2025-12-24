package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import org.springframework.http.ResponseEntity;

public interface VolumeSnapshotServicePort {
    PageResponse<VolumeSnapshotResponse> getSnapshots(PageRequest page, String userId, String projectId);
    VolumeSnapshotResponse getSnapshotDetails(String userId, String projectId, String snapshotId);
    ResponseEntity<Void> deleteSnapshot(String userId, String projectId, String snapshotId);
    VolumeSnapshotResponse createSnapshot(String userId, String projectId, VolumeSnapshotRequest request);
}
