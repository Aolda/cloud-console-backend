package com.acc.server.openstack.domain.port;

public interface KeystonePort {
    /**
     * Keystone의 /v3/auth/tokens를 호출해 X-Subject-Token을 반환
     */
    String issueToken(String username, String password, String projectName);
}