package com.acc.local.service.modules.auth.constant;

public class KeystoneRoutes {
	public static final String TOKEN_AUTH = "/v3/OS-FEDERATION/identity_providers/aolda/protocols/oidc/auth";
	public static final String GET_ASSIGNED_PERMISSIONS = "/v3/role_assignments?user.id={user_id}&effective&include_names=true";
}
