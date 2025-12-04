package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter @Setter
public class JwtProperties {

    private String secret;

    /** Access Token 만료 시간 (ms) */
    private Long expirationMs;

    /** Refresh Token 만료 시간 (ms) - 기본값 7일 */
    private Long refreshExpirationMs;

    /** OAuth 검증 토큰 만료 시간 (ms) - 기본값 15분 */
    private Long oauthVerificationExpirationMs;
}
