package com.acc.local.service.adapters.network;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.local.dto.network.CreateSubnetRequest;
import com.acc.local.dto.network.ViewSubnetsResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.network.NetworkUtil;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.ports.SubnetServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Primary
public class SubnetServiceAdapter implements SubnetServicePort {

    private final NeutronModule neutronModule;
    private final NetworkUtil networkUtil;
    private final AuthModule authModule;

    @Override
    public void createSubnet(CreateSubnetRequest request, String networkId, String projectId, String userId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        if (!networkUtil.validateResourceName(request.getSubnetName())) {
            throw new NetworkException(NetworkErrorCode.INVALID_SUBNET_NAME);
        }

        if (!networkUtil.validateCidr(request.getCidr())) {
            throw new NetworkException(NetworkErrorCode.INVALID_SUBNET_CIDR);
        }

        if (request.getGatewayIp() != null &&
                !networkUtil.validateIpv4(request.getGatewayIp())) {
            throw new NetworkException(NetworkErrorCode.INVALID_SUBNET_GATEWAY_IP);
        }

        neutronModule.createSubnet(token, List.of(request), networkId);
    }

    @Override
    public void deleteSubnet(String subnetId, String projectId, String userId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        if (!neutronModule.canDeleteSubnet(token, subnetId)) {
            throw new NetworkException(NetworkErrorCode.CAN_NOT_DELETE_SUBNET);
        }

        neutronModule.deleteSubnet(token, subnetId);

    }

    @Override
    public PageResponse<ViewSubnetsResponse> listSubnets(PageRequest page, String networkId, String projectId, String userId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        return neutronModule.listSubnets(token,
                networkId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

    @Override
    public ViewSubnetsResponse getSubnetDetail(String subnetId, String projectId, String userId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);
        return neutronModule.getSubnetDetails(token, subnetId);
    }
}
