package com.acc.local.dto.auth;

import java.time.LocalDateTime;
import java.util.List;

public record KeystoneTokenInfo(
	List<String> auditIds,
	LocalDateTime expiresAt,
	LocalDateTime issuedAt,
	String userId,
	String userName
) {}
