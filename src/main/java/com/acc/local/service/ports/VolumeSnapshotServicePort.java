package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import org.springframework.http.ResponseEntity;

public interface VolumeSnapshotServicePort {
    PageResponse<VolumeSnapshotResponse> getSnapshots(PageRequest page, String sessionId, String projectId);
    VolumeSnapshotResponse getSnapshotDetails(String sessionId, String projectId, String snapshotId);
    ResponseEntity<Void> deleteSnapshot(String sessionId, String projectId, String snapshotId);
    VolumeSnapshotResponse createSnapshot(String sessionId, String projectId, VolumeSnapshotRequest request);
}
