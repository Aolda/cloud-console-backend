package com.acc.local.service.adapters.network;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.ViewNetworksResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.network.NetworkUtil;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.ports.NetworkServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class NetworkServiceAdapter implements NetworkServicePort {

    private final NeutronModule neutronModule;
    private final NetworkUtil networkUtil;
    private final AuthModule authModule;

    @Override
    public void createNetwork(CreateNetworkRequest request, String userId, String projectId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        /* --- Quota 검증 --- */

        /* --- 네트워크 생성 --- */
        if (!networkUtil.validateResourceName(request.getNetworkName())) {
            throw new NetworkException(NetworkErrorCode.INVALID_NETWORK_NAME);
        }
        if (!networkUtil.validateNetworkMtu(request.getMtu())) {
            throw new NetworkException(NetworkErrorCode.INVALID_NETWORK_MTU);
        }
        String networkId = neutronModule.createGeneralNetwork(request, token);

        /* --- 서브넷 생성 --- */
        if (request.getSubnets() != null) {
            for (CreateNetworkRequest.Subnet subnet : request.getSubnets()) {
                if (!networkUtil.validateResourceName(subnet.getSubnetName())) {
                    throw new NetworkException(NetworkErrorCode.INVALID_SUBNET_NAME);
                }

                if (!networkUtil.validateCidr(subnet.getCidr())) {
                    throw new NetworkException(NetworkErrorCode.INVALID_SUBNET_CIDR);
                }
            }

            neutronModule.createSubnet(token, request.getSubnets(), networkId);
        }
    }

    @Override
    public void deleteNetwork(String networkId, String userId, String projectID) {
        String token = authModule.issueProjectScopeToken(projectID, userId);


        if (!neutronModule.canDeleteNetwork(token, networkId)) {
            throw new NetworkException(NetworkErrorCode.CAN_NOT_DELETE_NETWORK);
        }

        neutronModule.deleteNetwork(token, networkId);
    }

    @Override
    public PageResponse<ViewNetworksResponse> listNetworks(PageRequest page, String userId, String projectId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        /* --- 네트워크 리스트 조회 --- */
        return neutronModule.listNetworks(token,
                projectId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }
}
