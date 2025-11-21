package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateSecurityGroupRequest;
import com.acc.local.dto.network.ViewSecurityGroupsResponse;

public interface SecurityGroupServicePort {
    void createSecurityGroup(CreateSecurityGroupRequest request, String projectId, String userId);

    PageResponse<ViewSecurityGroupsResponse> listSecurityGroups(PageRequest page, String projectId, String userId);

    ViewSecurityGroupsResponse getSecurityGroupDetail(PageRequest page, String securityGroupId, String projectId, String userId);

    void deleteSecurityGroup(String securityGroupId, String projectId, String userId);
}
