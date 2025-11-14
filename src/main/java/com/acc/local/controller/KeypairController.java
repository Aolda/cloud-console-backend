package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.controller.docs.KeypairDocs;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairListResponse;
import com.acc.local.service.ports.KeypairServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KeypairController implements KeypairDocs {

    private final KeypairServicePort keypairServicePort;

    @Override
    public ResponseEntity<PageResponse<KeypairListResponse>> getKeypairs(String token, PageRequest page) {
        PageResponse<KeypairListResponse> response = keypairServicePort.getKeypairs(token, page);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CreateKeypairResponse> createKeypair(String token, CreateKeypairRequest request) {
        CreateKeypairResponse response = keypairServicePort.createKeypair(token, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Object> deleteKeypair(String token, String keypairId) {
        keypairServicePort.deleteKeypair(token, keypairId);
        return ResponseEntity.noContent().build();
    }
}
