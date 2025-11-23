package com.acc.local.service.adapters.quickstart;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.quickstart.QuickStartErrorCode;
import com.acc.global.exception.quickstart.QuickStartException;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.quickstart.QuickStartRequest;
import com.acc.local.dto.quickstart.QuickStartResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.instance.InstanceModule;
import com.acc.local.service.modules.instance.InstanceUtil;
import com.acc.local.service.modules.network.ApmModule;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.ports.QuickStartServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Primary
public class QuickStartServiceAdapter implements QuickStartServicePort {

    private final NeutronModule neutronModule;
    private final ApmModule apmModule;
    private final AuthModule authModule;
    private final InstanceModule instanceModule;
    private final InstanceUtil instanceUtil;

    @Override
    public QuickStartResponse create(String userId, String projectId, QuickStartRequest request) {
        String token = authModule.issueProjectScopeToken(projectId, userId);

        String defaultInterfaceId = null;
        String serverPort = null;
        String forwardingId = null;
        String networkId = neutronModule.getDefaultNetworkId(token, projectId);
        String sgId = neutronModule.getDefaultSecurityGroupId(token, projectId);

        /* --- dto 검증 --- */
        if (!instanceUtil.validateInstanceName(request.getInstanceName())) {
            throw new QuickStartException(QuickStartErrorCode.QUICK_START_INVALID_INSTANCE_NAME);
        }

        /* --- network 생성 --- */
        if (request.getIsExternal()) {
            try {
            defaultInterfaceId = neutronModule.createInterface(
                    token,
                    CreateInterfaceRequest.builder()
                            .interfaceName("default-interface")
                            .isExternal(true)
                            .networkId(networkId)
                            .securityGroupIds(List.of(sgId))
                            .build()
            ); } catch (AccBaseException e) {
                throw new QuickStartException(QuickStartErrorCode.QUICK_START_INTERFACE_CREATE_FAILED);
            }

            try {
            String externalIp = neutronModule.getExternalIpByInterfaceId(token, defaultInterfaceId).get("floating_ip_address");
                Map<String, String> forwardingResult = apmModule.createSSHForwarding(token, projectId, externalIp, defaultInterfaceId);
                serverPort = forwardingResult.get("serverPort");
                forwardingId = forwardingResult.get("id");
            } catch (AccBaseException e) {
                neutronModule.deleteInterface(token, defaultInterfaceId);
                throw new QuickStartException(QuickStartErrorCode.QUICK_START_FORWARDING_CREATE_FAILED);
            }
        }

        /* --- instance 생성 --- */
        String imageId = "ca11a35b-f858-438b-8705-1773752fea1a"; // ubuntu 이미지
        InstanceCreateRequest.InstanceCreateRequestBuilder instanceBuilder = InstanceCreateRequest.builder()
                .instanceName(request.getInstanceName())
                .typeId(request.getTypeId())
                .imageId(imageId)
                .diskSize(request.getVolumeSize())
                .password(request.getPassword());

        if (request.getIsExternal()) {
            instanceBuilder.interfaceIds(List.of(defaultInterfaceId));
        } else {
            instanceBuilder.networkIds(List.of(networkId));
            instanceBuilder.securityGroupIds(List.of(sgId));
        }

        try{
            instanceModule.createInstance(token, projectId, instanceBuilder.build());
        } catch (AccBaseException e) {
            if (request.getIsExternal()) {
                if (forwardingId != null) apmModule.deleteForwarding(token, forwardingId);
                if (defaultInterfaceId != null) neutronModule.deleteInterface(token, defaultInterfaceId);
            }
            throw new QuickStartException(QuickStartErrorCode.QUICK_START_INSTANCE_CREATION_FAILED);
        }

        /* --- 그 외 로직 --- */
        return QuickStartResponse.builder()
                .serverPort(serverPort).build();
    }
}
