package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.network.ViewInterfacesResponse;

public interface InterfaceServicePort {

    void createInterface(String userId, String projectId, CreateInterfaceRequest request);

    void deleteInterface(String userId, String projectId, String interfaceId);

    PageResponse<ViewInterfacesResponse> listInterfaces(PageRequest page, String userId, String projectId, String interfaceId, String networkId);

    void allocateExternalIp(String userID, String projectId, String interfaceId);

    void releaseExternalIp(String userId, String projectId, String interfaceId);

    void createSSHForwarding(String userId, String projectId, String interfaceId);

    void deleteSSHForwarding(String userId, String projectId, String interfaceId);
}
