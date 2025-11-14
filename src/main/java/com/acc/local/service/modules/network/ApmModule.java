package com.acc.local.service.modules.network;

import com.acc.local.external.ports.ApmForwardingExternalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApmModule {

    private final ApmForwardingExternalPort apmForwardingExternalPort;

    public Map<String, String> createSSHForwarding(String token, String projectId, String interfaceIp, String interfaceId) {
        return apmForwardingExternalPort.createSSHForwarding(token, projectId, interfaceIp, "id-" + interfaceId);
    }

    public void deleteForwarding(String token, String forwardingId) {
        apmForwardingExternalPort.deleteForwarding(token, forwardingId);
    }

    public String getForwardingId(String token, String projectId, String interfaceIp) {
        return apmForwardingExternalPort.getForwardingId(token, projectId, interfaceIp);
    }
}
