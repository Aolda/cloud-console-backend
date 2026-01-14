package com.acc.local.dto.project;

import com.acc.local.dto.auth.UserKeystoneDto;

import lombok.Builder;

@Builder
public record ProjectOwnerDto(
	String userId,
	String userName
) {
	public static ProjectOwnerDto from(UserKeystoneDto createdBy) {
		return ProjectOwnerDto.builder()
			.userId(createdBy.id())
			.userName(createdBy.name())
			.build();
	}
}
