package com.acc.local.dto.project;

import com.acc.local.dto.auth.UserKeystoneDto;

import lombok.Builder;

@Builder
public record ProjectOwnerDto(
	String userId,
	String userName,
	String userEmail
) {
	public static ProjectOwnerDto from(UserKeystoneDto createdBy) {
		return ProjectOwnerDto.builder()
			.userId(createdBy.id())
			.userName(createdBy.name())
			.userEmail(createdBy.email())
			.build();
	}
}
