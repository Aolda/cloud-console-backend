package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.ViewInterfacesResponse;

import java.util.List;
import java.util.Map;

public interface NeutronPortExternalPort {

    Map<String, String> callCreatePort(String keystoneToken, String networkId, String portName, String subnetId, List<String> securityGroupIds, String description);
    void callDeletePort(String keystoneToken, String portId);

    PageResponse<ViewInterfacesResponse> callListPorts(String keystoneToken,
                                                       String projectId,
                                                       String marker,
                                                       String direction,
                                                       int limit,
                                                       String deviceId,
                                                       String networkId);

    Map<String, String> getPortInfo(String keystoneToken, String portId);
}
