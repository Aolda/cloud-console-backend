package com.acc.local.controller;

import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.AuthDocs;
import com.acc.local.dto.auth.LoginedUserProfileResponse;
import com.acc.local.service.ports.AuthServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthDocs {


    private final AuthServicePort authServicePort;

    @Override
    public ResponseEntity<LoginedUserProfileResponse> getLoginUserInformation(Authentication authentication, String projectId) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String userId = principal.getKeystoneUserId();

        LoginedUserProfileResponse loginedUserProfileResponse = authServicePort.getUserLoginedProfile(userId, projectId);
        return ResponseEntity.ok(loginedUserProfileResponse);
    }
}
