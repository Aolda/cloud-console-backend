package com.acc.local.controller;


import com.acc.global.security.JwtUtils;
import com.acc.local.dto.volume.response.VolumeDetailResponse;
import com.acc.local.dto.volume.response.VolumeResponse;
import com.acc.local.service.ports.VolumeServicePort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.Volume;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/volume")
public class VolumeController {
    private final VolumeServicePort volumeService;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<VolumeResponse> getVolumes(HttpServletRequest req) {
        //필터에서 principal 로 넘겨 주는 방식은 어떨지 여쭤볼것
        String auth = req.getHeader("Authorization");
        String jwt = (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7) : null;
        String keystoneToken = (jwt != null) ? jwtUtils.getKeystoneTokenFromToken(jwt) : null;

        VolumeResponse res = volumeService.readVolume(keystoneToken);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<VolumeDetailResponse> getDetailVolumes(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        String jwt = (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7) : null;
        String keystoneToken = (jwt != null) ? jwtUtils.getKeystoneTokenFromToken(jwt) : null;

        VolumeDetailResponse res = volumeService.readVolumeDetail(keystoneToken);
        return ResponseEntity.ok(res);
    }
}
