package com.acc.local.service.adapters.network;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.local.dto.network.CreateRouterRequest;
import com.acc.local.dto.network.ViewRoutersResponse;
import com.acc.local.external.modules.neutron.NeutronAPIUtil;
import com.acc.local.service.modules.network.NetworkUtil;
import com.acc.local.service.modules.network.NeutronModule;
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

    @Override
    public void createRouter(CreateRouterRequest request, String token) {
        /* --- token 검증 ( 프로젝트, Role 권한 검증 ) --- */

        if (!networkUtil.validateResourceName(request.getRouterName())) {
            throw new NetworkException(NetworkErrorCode.INVALID_ROUTER_NAME);
        }
        if (!networkUtil.validateGateway(request.getGateway())) {
            throw new NetworkException(NetworkErrorCode.INVALID_ROUTER_GATEWAY);
        }

        neutronModule.createRouter(token, request.getRouterName(), request.getGateway());
    }

    @Override
    public void deleteRouter(String routerId, String token) {
        /* --- token 검증 ( 프로젝트, Role 권한 검증 ) --- */

        neutronModule.deleteRouter(token, routerId);
    }

    @Override
    public PageResponse<ViewRoutersResponse> listRouters(PageRequest page, String token) {
        /* --- token 검증 ( 프로젝트, Role 권한 검증 ) --- */

        return neutronModule.listRouters(token,
                "project_id",
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit());
    }

}
