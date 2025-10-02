package com.acc.local.service.ports;

import com.acc.local.dto.volume.snapshot.VolumeSnapshotsResponse;

public interface VolumeSnapshotServicePort {
    VolumeSnapshotsResponse getSnapshots(String token);
}
