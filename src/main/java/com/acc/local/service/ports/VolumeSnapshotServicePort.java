package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import org.springframework.http.ResponseEntity;

public interface VolumeSnapshotServicePort {
    PageResponse<VolumeSnapshotResponse> getSnapshots(PageRequest page, String token);
    VolumeSnapshotResponse getSnapshotDetails(String token, String snapshotId);
    ResponseEntity<Void> deleteSnapshot(String token, String snapshotId);
    VolumeSnapshotResponse createSnapshot(String token, VolumeSnapshotRequest request);
}
