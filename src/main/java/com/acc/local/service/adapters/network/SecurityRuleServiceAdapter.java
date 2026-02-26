package com.acc.local.service.adapters.network;

import com.acc.global.exception.network.NetworkErrorCode;
import com.acc.global.exception.network.NetworkException;
import com.acc.local.dto.network.CreateSecurityRuleRequest;
import com.acc.local.service.modules.network.NetworkUtil;
import com.acc.local.service.modules.network.NeutronModule;
import com.acc.local.service.modules.session.SessionModule;
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
    private final SessionModule sessionModule;

    @Override
    public String createSecurityRule(String projectId, String sessionId, CreateSecurityRuleRequest request) {

        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);

        if (networkUtil.isNullOrEmpty(request.getSecurityGroupId())) {
            throw new NetworkException(NetworkErrorCode.INVALID_SECURITY_GROUP_ID);
        }

        String protocol = networkUtil.validateProtocol(request.getProtocol().name());
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


        return neutronModule.createSecurityGroupRule(
                token,
                request.getSecurityGroupId(),
                direction,
                protocol,
                request.getPort(),
                request.getRemoteSecurityGroupId(),
                request.getCidr()
        );
    }

    @Override
    public void deleteSecurityRule(String srId, String projectId, String sessionId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        neutronModule.deleteSecurityGroupRule(token, srId);
    }
}
