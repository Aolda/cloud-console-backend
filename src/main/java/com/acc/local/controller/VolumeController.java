package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.VolumeDocs;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import com.acc.local.service.modules.auth.AuthModule;
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
    private final AuthModule authModule;

    @Override
    public ResponseEntity<PageResponse<VolumeResponse>> getVolumes(
            PageRequest page,
            Authentication authentication
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String projectId = jwtInfo.getProjectId();
        String keystoneToken = authModule.issueProjectScopeToken(projectId, jwtInfo.getUserId());
        
        PageResponse<VolumeResponse> response = volumeServicePort.getVolumes(page, projectId, keystoneToken);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<VolumeResponse> getVolumeDetails(String volumeId, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String projectId = jwtInfo.getProjectId();
        String keystoneToken = authModule.issueProjectScopeToken(projectId, jwtInfo.getUserId());
        
        VolumeResponse volumeDto = volumeServicePort.getVolumeDetails(projectId, keystoneToken, volumeId);
        return ResponseEntity.ok(volumeDto);
    }

    @Override
    public ResponseEntity<Void> deleteVolume(String volumeId, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String projectId = jwtInfo.getProjectId();
        String keystoneToken = authModule.issueProjectScopeToken(projectId, jwtInfo.getUserId());
        
        return volumeServicePort.deleteVolume(projectId, keystoneToken, volumeId);
    }

    @Override
    public ResponseEntity<VolumeResponse> createVolume(VolumeRequest request, Authentication authentication) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String projectId = jwtInfo.getProjectId();
        String keystoneToken = authModule.issueProjectScopeToken(projectId, jwtInfo.getUserId());
        
        VolumeResponse createdVolume = volumeServicePort.createVolume(projectId, keystoneToken, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdVolume);
    }
}
