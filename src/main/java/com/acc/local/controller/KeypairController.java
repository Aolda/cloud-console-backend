package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.KeypairDocs;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairListResponse;
import com.acc.local.service.ports.KeypairServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KeypairController implements KeypairDocs {

    private final KeypairServicePort keypairServicePort;

    @Override
    public ResponseEntity<PageResponse<KeypairListResponse>> getKeypairs(Authentication authentication, PageRequest page) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String projectId = jwtInfo.getProjectId();

        PageResponse<KeypairListResponse> response = keypairServicePort.getKeypairs(page, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CreateKeypairResponse> createKeypair(Authentication authentication, CreateKeypairRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        CreateKeypairResponse response = keypairServicePort.createKeypair(request, userId, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Object> deleteKeypair(Authentication authentication, String keypairId) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        keypairServicePort.deleteKeypair(keypairId, userId, projectId);
        return ResponseEntity.noContent().build();
    }
}
