package com.acc.local.dto.project;

import com.acc.local.domain.model.auth.User;

import lombok.Builder;

@Builder
public record ProjectOwnerDto(
	String userId,
	String userName
) {
	public static ProjectOwnerDto from(User createdBy) {
		return ProjectOwnerDto.builder()
			.userId(createdBy.getId())
			.userName(createdBy.getName())
			.build();
	}
}
