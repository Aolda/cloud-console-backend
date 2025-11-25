package com.acc.local.external.modules.keystone.constant;

public class KeystoneRoutes {
	public static final String TOKEN_AUTH_DEFAULT = "/v3/auth/tokens";
	public static final String PROJECT_AUTH_DEFAULT = "/v3/auth/projects";
	public static final String TOKEN_AUTH_FEDERATE = "/v3/OS-FEDERATION/identity_providers/aolda/protocols/oidc/auth";
	public static final String GET_ASSIGNED_PERMISSIONS = "/v3/role_assignments?user.id={user_id}&effective&include_names=true";
	public static final String LIST_ROLES = "/v3/roles";
	public static final String CREATE_ROLE = "/v3/roles";
	public static final String ROLE_ASSIGNMENTS = "/v3/role_assignments";
	public static final String CREATE_USER = "/v3/users";
	public static final String POST_USER = "/v3/users/{user_id}";
	public static final String PROJECT_DEFAULT = "/v3/projects";
	public static final String PROJECT_USER = "/v3/users/{user_id}/projects";
	public static final String POST_PROJECT = "/v3/projects/{project_id}";
	public static final String GET_ROLES = "/v3/roles";
	public static final String SET_ROLE = "/v3/projects/{project_id}/users/{user_id}/roles/{role_id}";
}
