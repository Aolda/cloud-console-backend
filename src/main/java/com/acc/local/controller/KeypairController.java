package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.security.session.SessionPrincipal;
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
    public ResponseEntity<PageResponse<KeypairListResponse>> getKeypairs(Authentication authentication, PageRequest page, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        PageResponse<KeypairListResponse> response = keypairServicePort.getKeypairs(page, sessionId, projectId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CreateKeypairResponse> createKeypair(Authentication authentication, CreateKeypairRequest request, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        CreateKeypairResponse response = keypairServicePort.createKeypair(request, sessionId, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Object> deleteKeypair(Authentication authentication, String keypairId, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        keypairServicePort.deleteKeypair(keypairId, sessionId, projectId);
        return ResponseEntity.noContent().build();
    }
}
