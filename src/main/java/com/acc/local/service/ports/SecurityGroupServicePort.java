package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateSecurityGroupRequest;
import com.acc.local.dto.network.ViewSecurityGroupsResponse;

public interface SecurityGroupServicePort {
    String createSecurityGroup(CreateSecurityGroupRequest request, String projectId, String sessionId);

    PageResponse<ViewSecurityGroupsResponse> listSecurityGroups(PageRequest page, String projectId, String sessionId);

    ViewSecurityGroupsResponse getSecurityGroupDetail(PageRequest page, String securityGroupId, String projectId, String sessionId);

    void deleteSecurityGroup(String securityGroupId, String projectId, String sessionId);
}
