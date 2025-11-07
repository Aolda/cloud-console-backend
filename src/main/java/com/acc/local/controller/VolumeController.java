package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.controller.docs.VolumeDocs;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import com.acc.local.service.ports.VolumeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VolumeController implements VolumeDocs {

    private final VolumeServicePort volumeServicePort;

    @Override
    public ResponseEntity<PageResponse<VolumeResponse>> getVolumes(
            String token,
            PageRequest page
    ) {
        PageResponse<VolumeResponse> response = volumeServicePort.getVolumes(page, token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<VolumeResponse> getVolumeDetails(String token, String volumeId) {
        VolumeResponse volumeDto = volumeServicePort.getVolumeDetails(token, volumeId);
        return ResponseEntity.ok(volumeDto);
    }

    @Override
    public ResponseEntity<Void> deleteVolume(String token, String volumeId) {
        return volumeServicePort.deleteVolume(token, volumeId);
    }

    @Override
    public ResponseEntity<VolumeResponse> createVolume(String token, VolumeRequest request) {
        VolumeResponse createdVolume = volumeServicePort.createVolume(token, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createdVolume);
    }
}