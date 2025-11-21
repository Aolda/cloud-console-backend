package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import org.springframework.http.ResponseEntity;

public interface VolumeSnapshotServicePort {
    PageResponse<VolumeSnapshotResponse> getSnapshots(PageRequest page, String projectId, String keystoneToken);
    VolumeSnapshotResponse getSnapshotDetails(String projectId, String keystoneToken, String snapshotId);
    ResponseEntity<Void> deleteSnapshot(String projectId, String keystoneToken, String snapshotId);
    VolumeSnapshotResponse createSnapshot(String projectId, String keystoneToken, VolumeSnapshotRequest request);
}
