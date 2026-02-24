package com.acc.local.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakTokens {
    private String accessToken;
    private String refreshToken;
    private String idToken;
    private LocalDateTime expiresAt;
}
