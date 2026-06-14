package com.acc.local.service.adapters.network;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.CreateSubnetRequest;
import com.acc.local.dto.network.ViewNetworksResponse;
import com.acc.local.service.modules.network.NetworkUtil;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.modules.session.SessionModule;
import com.acc.local.service.ports.NetworkServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Primary
public class NetworkServiceAdapter implements NetworkServicePort {

    private final NeutronModule neutronModule;
    private final NetworkUtil networkUtil;
    private final SessionModule sessionModule;

    @Override
    public String createNetwork(CreateNetworkRequest request, String sessionId, String projectId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        /* --- Quota 검증 --- */

        /* --- 네트워크 생성 --- */
        if (!networkUtil.validateResourceName(request.getNetworkName()) ||
        request.getNetworkName().equals("default-network")) {
            throw new NetworkException(NetworkErrorCode.INVALID_NETWORK_NAME);
        }
        if (request.getMtu() != null && !networkUtil.validateNetworkMtu(request.getMtu())) {
            throw new NetworkException(NetworkErrorCode.INVALID_NETWORK_MTU);
        }
        String networkId = neutronModule.createGeneralNetwork(request, token);

        /* --- 서브넷 생성 --- */
        if (request.getSubnets() != null) {
            for (CreateSubnetRequest subnet : request.getSubnets()) {
                if (!networkUtil.validateResourceName(subnet.getSubnetName())) {
                    throw new NetworkException(NetworkErrorCode.INVALID_SUBNET_NAME);
                }

                if (!networkUtil.validateCidr(subnet.getCidr())) {
                    throw new NetworkException(NetworkErrorCode.INVALID_SUBNET_CIDR);
                }

                if (subnet.getGatewayIp() != null &&
                        !networkUtil.validateIpv4(subnet.getGatewayIp())) {
                    throw new NetworkException(NetworkErrorCode.INVALID_SUBNET_GATEWAY_IP);
                }
            }

            List<String> subnetCidrs = request.getSubnets().stream().map(
                    CreateSubnetRequest::getCidr
            ).toList();
            if (networkUtil.hasOverlappingCidrs(subnetCidrs)) {
                throw new NetworkException(NetworkErrorCode.OVERLAPPING_SUBNET_CIDR);
            }

            neutronModule.createSubnet(token, request.getSubnets(), networkId);
        }

        return networkId;
    }

    @Override
    public void deleteNetwork(String networkId, String sessionId, String projectID) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectID);


        if (!neutronModule.canDeleteNetwork(token, networkId)) {
            throw new NetworkException(NetworkErrorCode.CAN_NOT_DELETE_NETWORK);
        }

        neutronModule.deleteNetwork(token, networkId);
    }

    @Override
    public PageResponse<ViewNetworksResponse> listNetworks(PageRequest page, String sessionId, String projectId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        /* --- 네트워크 리스트 조회 --- */
        return neutronModule.listNetworks(token,
                projectId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }
}
