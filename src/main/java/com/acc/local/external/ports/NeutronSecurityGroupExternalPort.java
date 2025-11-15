package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.ViewSecurityGroupsResponse;

public interface NeutronSecurityGroupExternalPort {
    void callCreateSecurityGroup(String keystoneToken, String projectId, String securityGroupName, String description);

    PageResponse<ViewSecurityGroupsResponse> callListSecurityGroups(String keystoneToken, String projectId, String marker, String direction, int limit);

    ViewSecurityGroupsResponse callGetSecurityGroupById(String keystoneToken, String securityGroupId, String marker, String direction, int limit);

    ViewSecurityGroupsResponse callGetSecurityGroupByName(String keystoneToken, String projectId, String securityGroupName);

    void callDeleteSecurityGroup(String keystoneToken, String securityGroupId);
}
