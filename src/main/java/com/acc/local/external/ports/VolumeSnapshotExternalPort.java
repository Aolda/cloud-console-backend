package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import org.springframework.http.ResponseEntity;

public interface VolumeSnapshotExternalPort {

    PageResponse<VolumeSnapshotResponse> callListSnapshots(String token, String projectId, String marker, int limit);

    VolumeSnapshotResponse callGetSnapshotDetails(String token,String projectId, String snapshotId);

    ResponseEntity<Void> callDeleteSnapshot(String token, String projectId,String snapshotId);

    VolumeSnapshotResponse callCreateSnapshot(String token, String projectId,VolumeSnapshotRequest request);
}
