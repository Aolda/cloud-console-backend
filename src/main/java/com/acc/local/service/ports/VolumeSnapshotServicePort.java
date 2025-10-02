package com.acc.local.service.ports;

import com.acc.local.dto.volume.snapshot.VolumeSnapshotsResponse;
import org.springframework.http.ResponseEntity;

public interface VolumeSnapshotServicePort {
    VolumeSnapshotsResponse getSnapshots(String token);
    ResponseEntity<Void> deleteSnapshot(String token, String snapshotId);
}
