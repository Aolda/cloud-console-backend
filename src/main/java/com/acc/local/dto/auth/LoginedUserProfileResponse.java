package com.acc.local.dto.auth;

import com.acc.local.dto.project.ProjectServiceDto;
import lombok.Builder;

@Builder
public record LoginedUserProfileResponse(
	String userName,
	UnivDepartBriefDto univ,
	ProjectServiceDto project
) {}
