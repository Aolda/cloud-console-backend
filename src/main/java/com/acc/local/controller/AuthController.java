package com.acc.local.controller;

import com.acc.local.dto.user.UserCreateRequest;
import com.acc.local.dto.user.UserResponse;
import com.acc.local.service.ports.AuthPort;
import com.acc.local.service.ports.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthPort authPort;
    private final UserPort userPort;

    @GetMapping("/token")
    public String issueToken() {
        return authPort.issueToken();
    }

    @PostMapping("/users")
    public UserResponse createUser(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody UserCreateRequest request) {
        return userPort.createUser(bearerToken, request);
    }
}
