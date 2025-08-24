package com.acc.local.domain.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum ProjectPermission {
	ROOT("admin"),
	CTRL("manage"),
	VIEW("guest"),
	NONE();

	private Set<String> keystoneRoleName;

	ProjectPermission() {}

	ProjectPermission(String... keystoneRoleNames) {
		if (keystoneRoleNames.length == 0) return;

		this.keystoneRoleName.addAll(Arrays.asList(keystoneRoleNames));
	}

	public static ProjectPermission findByKeystoneRoleName(String roleName) {
		if (roleName == null || roleName.isEmpty()) return NONE;

		for (ProjectPermission permission : values()) {
			if (permission.keystoneRoleName != null && permission.keystoneRoleName.contains(roleName)) {
				return permission;
			}
		}

		return NONE;
	}
}
