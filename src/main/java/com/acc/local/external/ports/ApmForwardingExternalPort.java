package com.acc.local.external.ports;

import java.util.Map;

public interface ApmForwardingExternalPort {
    Map<String, String> createSSHForwarding(String keystoneToken, String projectId, String instanceIp, String name);

    void deleteForwarding(String keystoneToken, String forwardingId);

    String getForwardingId(String keystoneToken, String projectId, String instanceIp);
}
