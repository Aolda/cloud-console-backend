package com.acc.local.dto.project;

import lombok.Builder;

@Builder
public record InvitableUser(
	String userName,
	String userEmail,
	String userId
) {}
