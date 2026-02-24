package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
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
            Authentication authentication,
            String projectId
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        PageResponse<VolumeResponse> response = volumeServicePort.getVolumes(page, sessionId, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<VolumeResponse> getVolumeDetails(String volumeId, Authentication authentication, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        VolumeResponse volumeDto = volumeServicePort.getVolumeDetails(sessionId, projectId, volumeId);
        return ResponseEntity.ok(volumeDto);
    }


    @Override
    public ResponseEntity<Void> deleteVolume(String volumeId, Authentication authentication, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        return volumeServicePort.deleteVolume(sessionId, projectId, volumeId);
    }

    @Override
    public ResponseEntity<VolumeResponse> createVolume(VolumeRequest request, Authentication authentication, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        VolumeResponse createdVolume = volumeServicePort.createVolume(sessionId, projectId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdVolume);
    }
}
