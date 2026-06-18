package com.acc.local.dto.auth;

import lombok.Builder;

@Builder
public record LoginedUserProfileResponse(
	String userName,
	UnivDepartBriefDto univ
) {}
