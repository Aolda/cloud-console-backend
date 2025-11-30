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
        return generateToken(userId, null);
    }

    public String generateToken(String userId, String projectId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationMs());

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());

        var builder = Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate);

        // projectId가 있으면 Claims에 추가
        if (projectId != null) {
            builder.claim("projectId", projectId);
        }

        return builder.signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (7 * 24 * 60 * 60 * 1000L)); // 7일

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

    public String getProjectIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("projectId", String.class);
        } catch (Exception e) {
            return null; // projectId가 없을 수 있음
        }
    }

    /**
     * 만료된 토큰에서도 projectId 추출
     * refresh 시 기존 토큰에서 projectId를 가져올 때 사용
     */
    public String getProjectIdFromExpiredToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("projectId", String.class);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return e.getClaims().get("projectId", String.class);
        } catch (Exception e) {
            return null;
        }
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
     * JWT 만료시간을 LocalDateTime으로 계산
     * 토큰 생성 시와 동일한 만료시간 적용
     */
    public LocalDateTime calculateExpirationDateTime() {
        return LocalDateTime.now().plus(jwtProperties.getExpirationMs(), ChronoUnit.MILLIS);
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

    /**
     * OAuth 인증 검증용 JWT 생성 (15분 만료)
     */
    public String generateOAuthVerificationToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (15 * 60 * 1000L)); // 15분

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * OAuth 검증 토큰 유효성 확인
     */
    public boolean validateOAuthVerificationToken(String token) {
        return validateToken(token);
    }

    /**
     * OAuth 검증 토큰에서 이메일 추출
     */
    public String extractEmailFromOAuthToken(String token) {
        return getUserIdFromToken(token);
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