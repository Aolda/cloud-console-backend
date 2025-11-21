package com.acc.local.service.adapters.network;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.network.ViewInterfacesResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.network.ApmModule;
import com.acc.local.service.modules.network.NetworkUtil;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.ports.InterfaceServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Primary
public class InterfaceServiceAdapter implements InterfaceServicePort {

    private final NetworkUtil networkUtil;
    private final NeutronModule neutronModule;
    private final ApmModule apmModule;
    private final AuthModule authModule;

    @Override
    public void createInterface(String userId, String projectId, CreateInterfaceRequest request) {
        String token = authModule.issueProjectScopeToken(projectId, userId);
        /* --- Quota 검증 --- */

        /* --- 인터페이스 생성 --- */
        if (!networkUtil.validateResourceName(request.getInterfaceName())) {
            throw new NetworkException(NetworkErrorCode.INVALID_INTERFACE_NAME);
        }

        if (networkUtil.isNullOrEmpty(request.getNetworkId())) {
            throw new NetworkException(NetworkErrorCode.NOT_NULL_INTERFACE_NETWORK_ID);
        }

        if (networkUtil.isNullOrEmpty(request.getSecurityGroupIds())) {
            throw new NetworkException(NetworkErrorCode.NOT_NULL_INTERFACE_SECURITY_GROUP_IDS);
        }

        if (networkUtil.isNullOrEmpty(request.getExternal())) {
            throw new NetworkException(NetworkErrorCode.NOT_NULL_INTERFACE_EXTERNAL);
        }

        String interfaceId = neutronModule.createInterface(token, request);

        /* --- 외부 네트워크 연결 시, Floating IP 할당 --- */
        if (request.getExternal()) {
            String providerNetworkId = neutronModule.getProviderNetworkId(token);

            /* --- Floating IP 할당 실패 시, 생성된 인터페이스 삭제 처리 --- */
            if (!neutronModule.allocateExternalIpToInterface(token, providerNetworkId, interfaceId)) {
                neutronModule.deleteInterface(token, interfaceId);
                throw new NetworkException(NetworkErrorCode.EXTERNAL_IP_ALLOCATION_FAILED);
            }
        }
    }

    @Override
    public void deleteInterface(String userId, String projectId, String interfaceId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        if (networkUtil.isNullOrEmpty(interfaceId)) {
            throw new NetworkException(NetworkErrorCode.NOT_NULL_INTERFACE_ID);
        }

        /* --- 외부 IP 할당 해제 --- */
        Map<String, String> externalIpInfo = neutronModule.getExternalIpByInterfaceId(token, interfaceId);
        if (externalIpInfo != null) {

            /* --- SSH 포트포워딩 삭제 --- */
            String forwardingId = apmModule.getForwardingId(token,
                    projectId,
                    externalIpInfo.get("floating_ip_address"));
            if (forwardingId != null) {
                apmModule.deleteForwarding(token, forwardingId);
            }

            neutronModule.releaseExternalIpFromInterface(token, externalIpInfo.get("id"));
        }

        neutronModule.deleteInterface(token, interfaceId);
    }

    @Override
    public PageResponse<ViewInterfacesResponse> listInterfaces(PageRequest page, String userId, String projectId, String interfaceId, String networkId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        return neutronModule.listInterfaces(token,
                projectId,
                page.getMarker(),
                page.getDirection().name().equals("prev") ? "prev" : "next",
                page.getLimit(),
                interfaceId,
                networkId);
    }

    @Override
    public void allocateExternalIp(String userID, String projectId, String interfaceId) {
        String token = authModule.issueProjectScopeToken(projectId, userID);

        if (networkUtil.isNullOrEmpty(interfaceId)) {
            throw new NetworkException(NetworkErrorCode.NOT_NULL_INTERFACE_ID);
        }
        if (neutronModule.getExternalIpByInterfaceId(token, interfaceId) != null) {
            throw new NetworkException(NetworkErrorCode.ALREADY_HAS_EXTERNAL_IP);
        }

        String providerNetworkId = neutronModule.getProviderNetworkId(token);
        neutronModule.allocateExternalIpToInterface(token, providerNetworkId, interfaceId);
    }

    @Override
    public void releaseExternalIp(String userId, String projectId, String interfaceId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        if (networkUtil.isNullOrEmpty(interfaceId)) {
            throw new NetworkException(NetworkErrorCode.NOT_NULL_INTERFACE_ID);
        }

        Map<String, String> externalIpInfo = neutronModule.getExternalIpByInterfaceId(token, interfaceId);
        if (externalIpInfo == null) {
            throw new NetworkException(NetworkErrorCode.HAS_NOT_EXTERNAL_IP);
        }

        /* --- SSH 포트포워딩 삭제 --- */
        String forwardingId = apmModule.getForwardingId(token,
                projectId,
                externalIpInfo.get("floating_ip_address"));
        if (forwardingId != null) {
            apmModule.deleteForwarding(token, forwardingId);
        }

        neutronModule.releaseExternalIpFromInterface(token, externalIpInfo.get("id"));
    }

    @Override
    public void createSSHForwarding(String userId, String projectId, String interfaceId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        if (networkUtil.isNullOrEmpty(interfaceId)) {
            throw new NetworkException(NetworkErrorCode.NOT_NULL_INTERFACE_ID);
        }

        Map<String, String> externalIpInfo = neutronModule.getExternalIpByInterfaceId(token, interfaceId);
        if (externalIpInfo == null) {
            throw new NetworkException(NetworkErrorCode.HAS_NOT_EXTERNAL_IP);
        }

        apmModule.createSSHForwarding(token, projectId, externalIpInfo.get("floating_ip_address"), interfaceId);
    }

    @Override
    public void deleteSSHForwarding(String userId, String projectId, String interfaceId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        if (networkUtil.isNullOrEmpty(interfaceId)) {
            throw new NetworkException(NetworkErrorCode.NOT_NULL_INTERFACE_ID);
        }

        Map<String, String> externalIpInfo = neutronModule.getExternalIpByInterfaceId(token, interfaceId);
        if (externalIpInfo == null) {
            throw new NetworkException(NetworkErrorCode.HAS_NOT_EXTERNAL_IP);
        }

        /* --- SSH 포트포워딩 삭제 --- */
        String forwardingId = apmModule.getForwardingId(token,
                projectId,
                externalIpInfo.get("floating_ip_address"));
        if (forwardingId == null) {
            throw new NetworkException(NetworkErrorCode.NOT_FOUND_SSH_FORWARDING);
        }

        apmModule.deleteForwarding(token, forwardingId);
    }
}
