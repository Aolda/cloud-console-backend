package com.acc.server.openstack.domain.port;

import com.acc.server.openstack.domain.model.Project;

import java.util.List;

public interface KeystonePort {
    /**
     * Keystone의 /v3/auth/tokens를 호출해 X-Subject-Token을 반환
     */
    String issueToken(String username, String password, String projectName);

    List<Project> listProjects(String token);

}