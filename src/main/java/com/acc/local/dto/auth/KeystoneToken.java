package com.acc.local.dto.auth;

import java.time.LocalDateTime;
import java.util.List;

import com.acc.local.domain.enums.auth.KeystoneTokenType;

import lombok.Builder;

@Builder
public record KeystoneToken(
	KeystoneTokenType tokenType,
	List<String> auditIds,
	LocalDateTime expiresAt,
	LocalDateTime issuedAt,
	String userId,
	String userName,
	String token,
	Boolean isAdmin
) {}
