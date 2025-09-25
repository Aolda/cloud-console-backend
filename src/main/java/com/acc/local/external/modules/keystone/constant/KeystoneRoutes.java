package com.acc.local.external.modules.keystone.constant;

public class KeystoneRoutes {
	public static final String TOKEN_AUTH_DEFAULT = "/v3/auth/tokens";
	public static final String TOKEN_AUTH_FEDERATE = "/v3/OS-FEDERATION/identity_providers/aolda/protocols/oidc/auth";
	public static final String GET_ASSIGNED_PERMISSIONS = "/v3/role_assignments?user.id={user_id}&effective&include_names=true";
	public static final String CREATE_USER = "/v3/users";
	public static final String POST_USER = "/v3/users/{user_id}";
	public static final String CREATE_PROJECT = "/v3/projects";
	public static final String POST_PROJECT = "/v3/projects/{project_id}";
}
