package com.acc.local.dto.project;

import com.acc.local.domain.model.auth.KeystoneUser;

import lombok.Builder;

@Builder
public record ProjectOwnerDto(
	String userId,
	String userName
) {
	public static ProjectOwnerDto from(KeystoneUser createdBy) {
		return ProjectOwnerDto.builder()
			.userId(createdBy.getId())
			.userName(createdBy.getName())
			.build();
	}
}
