package com.acc.local.controller;

import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.QuickStartDocs;
import com.acc.local.dto.quickstart.QuickStartRequest;
import com.acc.local.service.ports.QuickStartServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QuickStartController implements QuickStartDocs {

    private final QuickStartServicePort quickStartServicePort;

    @Override
    public ResponseEntity<Object> create(
            Authentication authentication,
            QuickStartRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();

        return ResponseEntity.ok(quickStartServicePort.create(jwtInfo.getUserId(), jwtInfo.getProjectId(), request));
    }

}
