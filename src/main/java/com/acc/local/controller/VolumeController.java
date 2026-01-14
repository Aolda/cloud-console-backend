package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.VolumeDocs;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import com.acc.local.service.ports.VolumeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VolumeController implements VolumeDocs {

    private final VolumeServicePort volumeServicePort;

    @Override
    public ResponseEntity<PageResponse<VolumeResponse>> getVolumes(
            PageRequest page,
<<<<<<< HEAD
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
=======
            Authentication authentication,
            String projectId
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
>>>>>>> origin/main

        PageResponse<VolumeResponse> response = volumeServicePort.getVolumes(page, userId, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
<<<<<<< HEAD
    public ResponseEntity<VolumeResponse> getVolumeDetails(String volumeId, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
=======
    public ResponseEntity<VolumeResponse> getVolumeDetails(String volumeId, Authentication authentication, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
>>>>>>> origin/main

        VolumeResponse volumeDto = volumeServicePort.getVolumeDetails(userId, projectId, volumeId);
        return ResponseEntity.ok(volumeDto);
    }

    @Override
<<<<<<< HEAD
    public ResponseEntity<Void> deleteVolume(String volumeId, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
=======
    public ResponseEntity<Void> deleteVolume(String volumeId, Authentication authentication, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
>>>>>>> origin/main

        return volumeServicePort.deleteVolume(userId, projectId, volumeId);
    }

    @Override
<<<<<<< HEAD
    public ResponseEntity<VolumeResponse> createVolume(VolumeRequest request, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
=======
    public ResponseEntity<VolumeResponse> createVolume(VolumeRequest request, Authentication authentication, String projectId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
>>>>>>> origin/main

        VolumeResponse createdVolume = volumeServicePort.createVolume(userId, projectId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdVolume);
    }
}
