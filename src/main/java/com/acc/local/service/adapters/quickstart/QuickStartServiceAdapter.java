package com.acc.local.service.adapters.quickstart;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.quickstart.QuickStartErrorCode;
import com.acc.global.exception.quickstart.QuickStartException;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.quickstart.QuickStartRequest;
import com.acc.local.dto.quickstart.QuickStartResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.network.ApmModule;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.ports.QuickStartServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Primary
public class QuickStartServiceAdapter implements QuickStartServicePort {

    private final NeutronModule neutronModule;
    private final ApmModule apmModule;
    private final AuthModule authModule;

    public QuickStartResponse create(String userId, String projectId, QuickStartRequest request) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        String defaultInterfaceId = null;
        String serverPort = null;
        String networkId = neutronModule.getDefaultNetworkId(token, projectId);

        /* --- dto 검증 --- */

        /* --- network 생성 --- */
        if (request.getIsExternal()) {
            String sgId = neutronModule.getDefaultSecurityGroupId(token, projectId);
            try {
            defaultInterfaceId = neutronModule.createInterface(
                    token,
                    CreateInterfaceRequest.builder()
                            .interfaceName("default-interface")
                            .external(true)
                            .networkId(networkId)
                            .securityGroupIds(List.of(sgId))
                            .build()
            ); } catch (AccBaseException e) {
                throw new QuickStartException(QuickStartErrorCode.QUICK_START_INTERFACE_CREATE_FAILED);
            }

            try {
            String externalIp = neutronModule.getExternalIpByInterfaceId(token, defaultInterfaceId).get("floating_ip_address");
            serverPort = apmModule.createSSHForwarding(token, projectId, externalIp, defaultInterfaceId).get("serverPort");
            } catch (AccBaseException e) {
                neutronModule.deleteInterface(token, defaultInterfaceId);
                throw new QuickStartException(QuickStartErrorCode.QUICK_START_FORWARDING_CREATE_FAILED);
            }
        }

        /* --- instance 생성 --- */

        /* --- 그 외 로직 및 예외 처리 --- */
        return QuickStartResponse.builder()
                .serverPort(serverPort).build();
    }
}
