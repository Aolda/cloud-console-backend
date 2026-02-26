package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.network.ViewInterfacesResponse;

public interface InterfaceServicePort {

    String createInterface(String sessionId, String projectId, CreateInterfaceRequest request);

    void deleteInterface(String sessionId, String projectId, String interfaceId);

    PageResponse<ViewInterfacesResponse> listInterfaces(PageRequest page, String sessionId, String projectId, String interfaceId, String networkId);

    void allocateExternalIp(String sessionId, String projectId, String interfaceId);

    void releaseExternalIp(String sessionId, String projectId, String interfaceId);

    void createSSHForwarding(String sessionId, String projectId, String interfaceId);

    void deleteSSHForwarding(String sessionId, String projectId, String interfaceId);
}
