package com.acc.local.controller;

import com.acc.global.properties.KeycloakProperties;
import com.acc.local.service.ports.AuthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthPort authPort;
    private final KeycloakProperties keycloakProperties;

    @GetMapping("/token")
    public String issueToken() {
        return authPort.issueKeystoneToken();
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String keycloakLoginUrl = keycloakProperties.getLoginUrl();
        return ResponseEntity.status(302)
                .location(URI.create(keycloakLoginUrl))
                .build();
    }

    @GetMapping("/login/authorize")
    public String authorize(@RequestParam String userId, @RequestParam String keycloakToken) {
        return authPort.authenticateAndGenerateJwt(userId, keycloakToken).block();
    }
}
