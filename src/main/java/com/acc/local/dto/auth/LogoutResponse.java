package com.acc.local.dto.auth;

import lombok.Builder;

@Builder
public record LogoutResponse(
	String message
) {
	public static LogoutResponse success() {
		return LogoutResponse.builder()
			.message("로그아웃되었습니다.")
			.build();
	}
}