package com.acc.local.service.adapters.network;

import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.local.dto.network.CreateSecurityRuleRequest;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.network.NetworkUtil;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.ports.SecurityRuleServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Primary
public class SecurityRuleServiceAdapter implements SecurityRuleServicePort {

    private final NeutronModule neutronModule;
    private final NetworkUtil networkUtil;
    private final AuthModule authModule;

    @Override
    public void createSecurityRule(String sgId, String projectId, String userId, CreateSecurityRuleRequest request) {

        String token = authModule.issueProjectScopeToken(projectId, userId);

        if (networkUtil.isNullOrEmpty(sgId)) {
            throw new NetworkException(NetworkErrorCode.INVALID_SECURITY_GROUP_ID);
        }

        String protocol = networkUtil.validateProtocol(request.getProtocol());
        if (protocol == null) {
            throw new NetworkException(NetworkErrorCode.INVALID_SECURITY_RULE_PROTOCOL);
        }

        String direction = networkUtil.validateDirection(request.getDirection());
        if (direction == null) {
            throw new NetworkException(NetworkErrorCode.INVALID_SECURITY_RULE_DIRECTION);
        }

        if (!networkUtil.validatePortRange(request.getPort())) {
            throw new NetworkException(NetworkErrorCode.INVALID_SECURITY_RULE_PORT_RANGE);
        }

        if (!networkUtil.hasValidRemoteSecurityGroupIdOrCidr(request.getRemoteSecurityGroupId(), request.getCidr())) {
            throw new NetworkException(NetworkErrorCode.INVALID_SECURITY_RULE_SECURITY_GROUP_ID_OR_CIDR);
        }


        neutronModule.createSecurityGroupRule(
                token,
                sgId,
                direction,
                protocol,
                request.getPort(),
                request.getRemoteSecurityGroupId(),
                request.getCidr()
        );
    }

    @Override
    public void deleteSecurityRule(String srId, String projectId, String userId) {
        String token = authModule.issueProjectScopeToken(projectId, userId);
        neutronModule.deleteSecurityGroupRule(token, srId);
    }
}
