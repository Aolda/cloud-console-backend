package com.acc.local.external.ports;

import java.util.Map;

public interface NeutronFloatingIpExternalPort {
    void allocateFloatingIpToPort(String keystoneToken, String floatingNetworkId, String portId);

    Map<String, String> getFloatingIpInfo(String keystoneToken, String portId);

    void releaseFloatingIpFromPort(String keystoneToken, String floatingIpId);
}
