package com.acc.local.service.adapters.volume.snapshot;

import com.acc.local.dto.volume.snapshot.VolumeSnapshotsResponse;
import com.acc.local.service.modules.volume.snapshot.VolumeSnapshotModule;
import com.acc.local.service.ports.VolumeSnapshotServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class VolumeSnapshotServiceAdapter implements VolumeSnapshotServicePort {

    private final VolumeSnapshotModule volumeSnapshotModule;

    @Override
    public VolumeSnapshotsResponse getSnapshots(String token){
        return volumeSnapshotModule.getSnapshots(token);
    }
}
