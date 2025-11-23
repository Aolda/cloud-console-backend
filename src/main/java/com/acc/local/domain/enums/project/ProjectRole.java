package com.acc.local.domain.enums.project;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public enum ProjectRole {
	NONE("PROJECT_ROLE/NO_ROLE"),
	PROJECT_ADMIN("PROJECT_ROLE/ADMIN", "admin"),
	MANAGER("PROJECT_ROLE/MANAGER", "manage"),
	MEMBER("PROJECT_ROLE/MEMBER", "guest")
	;

	private String roleId;
	private Set<String> keystoneRoleNames = new HashSet<>();

	ProjectRole() {}

	ProjectRole(String roleId) {
		this.roleId = roleId;
	}

	ProjectRole(String roleId, String... keystoneRoleNames) {
		if (keystoneRoleNames.length == 0) return;

		this.roleId = roleId;
		this.keystoneRoleNames.addAll(Arrays.asList(keystoneRoleNames));
	}

	public static ProjectRole findByKeystoneRoleName(String roleId) {
		if (roleId == null || roleId.isEmpty()) return NONE;

		for (ProjectRole permission : values()) {
			if (permission.keystoneRoleNames != null && permission.keystoneRoleNames.contains(roleId)) {
				return permission;
			}
		}

		return NONE;
	}
}
