package com.acc.local.dto.auth;

import lombok.Builder;

/**
 * 프로젝트 진입 토큰 응답 DTO
 * projectId가 포함된 Access Token을 반환
 */
@Builder
public record ProjectTokenResponse(
    String accessToken
) {

}