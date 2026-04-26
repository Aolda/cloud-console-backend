package com.acc.local.service.adapters.network;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.dto.network.CreateRouterRequest;
import com.acc.local.dto.network.ViewRoutersResponse;
import com.acc.local.service.modules.network.NetworkUtil;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.modules.session.SessionModule;
import com.acc.local.service.ports.RouterServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class RouterServiceAdapter implements RouterServicePort {

    private final NeutronModule neutronModule;
    private final NetworkUtil networkUtil;
    private final SessionModule sessionModule;

    @Override
    public String createRouter(CreateRouterRequest request, String sessionId, String projectId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        if (!networkUtil.validateResourceName(request.getRouterName()) || request.getRouterName().equals("default-router")) {
            throw new NetworkException(NetworkErrorCode.INVALID_ROUTER_NAME);
        }
        if (!networkUtil.validateGateway(request.getIsExternal())) {
            throw new NetworkException(NetworkErrorCode.INVALID_ROUTER_GATEWAY);
        }

        return neutronModule.createRouter(token, request.getRouterName(), request.getIsExternal());
    }

    @Override
    public void deleteRouter(String routerId, String sessionId, String projectId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        if (!neutronModule.canDeleteRouter(token, routerId)) {
            throw new NetworkException(NetworkErrorCode.CAN_NOT_DELETE_ROUTER);
        }

        neutronModule.deleteRouter(token, routerId);
    }

    @Override
    public PageResponse<ViewRoutersResponse> listRouters(PageRequest page, String sessionId, String projectId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        return neutronModule.listRouters(token,
                projectId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

    @Override
    public void connectRouterToSubnet(String routerId, String subnetId, String sessionId, String projectId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        try {
            neutronModule.connectRouterToSubnet(token, routerId, subnetId);
        } catch (NeutronException e) {
            if (e.getErrorCode() == NeutronErrorCode.NEUTRON_ROUTER_CONFLICT) {
                String interfaceId = neutronModule.createInterfaceBySubnetId(token, subnetId);
                neutronModule.attachInterfaceToRouter(token, routerId, interfaceId);

            }
            else throw e;
        }
    }

    @Override
    public void disconnectRouterFromSubnet(String routerId, String subnetId, String sessionId, String projectId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        if (!neutronModule.canDisconnectRouterFromSubnet(token, routerId, subnetId)) {
            throw new NetworkException(NetworkErrorCode.CAN_NOT_DISCONNECT_ROUTER_FROM_SUBNET);
        }

        neutronModule.disconnectRouterFromSubnet(token, routerId, subnetId);
    }
}
