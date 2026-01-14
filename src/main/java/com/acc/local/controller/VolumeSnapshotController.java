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
<<<<<<< HEAD
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
        PageResponse<VolumeSnapshotResponse> response = volumeSnapshotServicePort.getSnapshots(page, userId, projectId);
=======
            Authentication authentication,
            String projectId
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        PageResponse<VolumeSnapshotResponse> response = volumeSnapshotServicePort.getSnapshots(page, jwtInfo.getUserId(), projectId);
>>>>>>> origin/main
        return ResponseEntity.ok(response);
    }

    @Override
<<<<<<< HEAD
    public ResponseEntity<VolumeSnapshotResponse> getSnapshotDetails(@RequestParam String snapshotId, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
        VolumeSnapshotResponse snapshotDto = volumeSnapshotServicePort.getSnapshotDetails(userId, projectId, snapshotId);
=======
    public ResponseEntity<VolumeSnapshotResponse> getSnapshotDetails(@RequestParam String snapshotId, Authentication authentication, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        VolumeSnapshotResponse snapshotDto = volumeSnapshotServicePort.getSnapshotDetails(jwtInfo.getUserId(), projectId, snapshotId);
>>>>>>> origin/main
        return ResponseEntity.ok(snapshotDto);
    }
    @Override
    public ResponseEntity<Void> deleteSnapshot(
<<<<<<< HEAD
            @RequestParam String snapshotId, Authentication authentication) {
=======
            @RequestParam String snapshotId, Authentication authentication, String projectId) {
>>>>>>> origin/main
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        return volumeSnapshotServicePort.deleteSnapshot(jwtInfo.getUserId(), projectId, snapshotId);
    }
    @Override
<<<<<<< HEAD
    public ResponseEntity<VolumeSnapshotResponse> createSnapshot(VolumeSnapshotRequest request, Authentication authentication){
=======
    public ResponseEntity<VolumeSnapshotResponse> createSnapshot(VolumeSnapshotRequest request, Authentication authentication, String projectId){
>>>>>>> origin/main
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        VolumeSnapshotResponse createdSnapshot = volumeSnapshotServicePort.createSnapshot(jwtInfo.getUserId(), projectId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdSnapshot);
    }

}
