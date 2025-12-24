package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.VolumeSnapshotDocs;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotRequest;
import com.acc.local.dto.volume.snapshot.VolumeSnapshotResponse;
import com.acc.local.service.ports.VolumeSnapshotServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class VolumeSnapshotController implements VolumeSnapshotDocs {

    private final VolumeSnapshotServicePort volumeSnapshotServicePort;

    @Override
    public ResponseEntity<PageResponse<VolumeSnapshotResponse>> getSnapshots(
            PageRequest page,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
        PageResponse<VolumeSnapshotResponse> response = volumeSnapshotServicePort.getSnapshots(page, userId, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<VolumeSnapshotResponse> getSnapshotDetails(@RequestParam String snapshotId, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
        VolumeSnapshotResponse snapshotDto = volumeSnapshotServicePort.getSnapshotDetails(userId, projectId, snapshotId);
        return ResponseEntity.ok(snapshotDto);
    }
    @Override
    public ResponseEntity<Void> deleteSnapshot(
            @RequestParam String snapshotId, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
        return volumeSnapshotServicePort.deleteSnapshot(userId, projectId, snapshotId);
    }
    @Override
    public ResponseEntity<VolumeSnapshotResponse> createSnapshot(VolumeSnapshotRequest request, Authentication authentication){
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
        VolumeSnapshotResponse createdSnapshot = volumeSnapshotServicePort.createSnapshot(userId, projectId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdSnapshot);
    }

}
