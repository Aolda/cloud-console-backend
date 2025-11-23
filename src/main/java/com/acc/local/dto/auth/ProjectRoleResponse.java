package com.acc.local.dto.auth;

import com.acc.local.domain.enums.project.ProjectRole;

import lombok.Builder;

@Builder
public record ProjectRoleResponse(
	String roleId
) {
	public static ProjectRoleResponse from(ProjectRole projectRole) {
		return ProjectRoleResponse.builder()
				.roleId(projectRole.getRoleId())
				.build();
	}
}
