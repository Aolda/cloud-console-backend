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

    private Claims getClaimsFromToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}