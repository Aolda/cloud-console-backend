package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.controller.docs.VolumeSnapshotDocs;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import com.acc.local.service.ports.VolumeSnapshotServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VolumeSnapshotController implements VolumeSnapshotDocs {

    private final VolumeSnapshotServicePort volumeSnapshotServicePort;

    @Override
    public ResponseEntity<PageResponse<VolumeSnapshotResponse>> getSnapshots(
            String token,
            PageRequest page
    ) {
        PageResponse<VolumeSnapshotResponse> response = volumeSnapshotServicePort.getSnapshots(page,token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<VolumeSnapshotResponse> getSnapshotDetails(String token, @PathVariable String snapshotId) {
        VolumeSnapshotResponse snapshotDto = volumeSnapshotServicePort.getSnapshotDetails(token, snapshotId);
        return ResponseEntity.ok(snapshotDto);
    }
    @Override
    public ResponseEntity<Void> deleteSnapshot(
            String token, String snapshotId) {
        return volumeSnapshotServicePort.deleteSnapshot(token, snapshotId);
    }
    @Override
    public ResponseEntity<VolumeSnapshotResponse> createSnapshot(String token, VolumeSnapshotRequest request){
        VolumeSnapshotResponse createdSnapshot = volumeSnapshotServicePort.createSnapshot(token, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdSnapshot);
    }

}
