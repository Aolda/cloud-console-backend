package com.acc.global.security.jwt;

import com.acc.global.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@RequiredArgsConstructor
@Component
@Slf4j
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public String extractUserIdFromKeycloakToken(String keycloakToken) {
        return Jwts.parser()
                .build()
                .parseUnsecuredClaims(keycloakToken) // 단순히 sub 값만 추출하는 방식
                .getPayload()
                .getSubject();
    }

    public static String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Authorization header must start with 'Bearer '");
    }



    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationMs());

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());

        var builder = Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate);

        return builder.signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshExpirationMs());

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        return Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Access Token 만료시간을 LocalDateTime으로 계산
     */
    public LocalDateTime calculateExpirationDateTime() {
        return LocalDateTime.now().plus(jwtProperties.getExpirationMs(), ChronoUnit.MILLIS);
    }

    /**
     * Refresh Token 만료시간을 LocalDateTime으로 계산
     */
    public LocalDateTime calculateRefreshTokenExpirationDateTime() {
        return LocalDateTime.now().plus(jwtProperties.getRefreshExpirationMs(), ChronoUnit.MILLIS);
    }

    /**
     * Refresh Token 만료시간을 초(seconds) 단위로 반환 (쿠키 설정용)
     */
    public int getRefreshTokenExpirationSeconds() {
        return (int) (jwtProperties.getRefreshExpirationMs() / 1000);
    }

    /**
     * Refresh Token 검증
     */
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken);
    }

    /**
     * Refresh Token에서 userId 추출
     */
    public String extractUserIdFromRefreshToken(String refreshToken) {
        return getUserIdFromToken(refreshToken);
    }

    private Claims getClaimsFromToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
