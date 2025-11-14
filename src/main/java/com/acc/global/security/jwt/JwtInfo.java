package com.acc.global.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.security.Principal;

/**
 * JWT 토큰에서 추출한 사용자 인증 정보
 * Spring Security의 Authentication.getPrincipal()에서 사용
 */
@Getter
@Builder
@AllArgsConstructor
public class JwtInfo implements Principal {

    private final String userId;
    private final String projectId;  // nullable - 로그인 시점에는 없을 수 있음

    @Override
    public String getName() {
        return userId; // Authentication.getName() 호환성 유지
    }
}
