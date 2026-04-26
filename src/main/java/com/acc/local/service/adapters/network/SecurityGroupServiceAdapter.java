package com.acc.local.service.adapters.network;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.local.dto.network.CreateSecurityGroupRequest;
import com.acc.local.dto.network.ViewSecurityGroupsResponse;
import com.acc.local.service.modules.network.NetworkUtil;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.modules.session.SessionModule;
import com.acc.local.service.ports.SecurityGroupServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class SecurityGroupServiceAdapter implements SecurityGroupServicePort {

    private final SessionModule sessionModule;
    private final NetworkUtil networkUtil;
    private final NeutronModule neutronModule;

    @Override
    public String createSecurityGroup(CreateSecurityGroupRequest request, String projectId, String sessionId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        if (!networkUtil.validateResourceName(request.getSecurityGroupName())
        || networkUtil.isDefaultSecurityGroup(request.getSecurityGroupName())) {
            throw new NetworkException(NetworkErrorCode.INVALID_SECURITY_GROUP_NAME);
        }

        return neutronModule.createSecurityGroup(token, projectId, request.getSecurityGroupName(), request.getDescription());
    }

    @Override
    public PageResponse<ViewSecurityGroupsResponse> listSecurityGroups(PageRequest page, String projectId, String sessionId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        return neutronModule.listSecurityGroups(token,
                projectId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

    @Override
    public ViewSecurityGroupsResponse getSecurityGroupDetail(PageRequest page, String securityGroupId, String projectId, String sessionId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        return neutronModule.getSecurityGroupDetails(token,
                securityGroupId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

    @Override
    public void deleteSecurityGroup(String securityGroupId, String projectId, String sessionId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        ViewSecurityGroupsResponse sg = neutronModule.getSecurityGroupDetails(
                token,
                securityGroupId,
                null,
                "next",
                0
        );
        if (networkUtil.isDefaultSecurityGroup(sg.getSecurityGroupName())) {
            throw new NetworkException(NetworkErrorCode.INVALID_SECURITY_GROUP_NAME);
        }

        neutronModule.deleteSecurityGroup(token, securityGroupId);
    }
}
