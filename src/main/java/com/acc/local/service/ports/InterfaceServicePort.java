package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.network.ViewInterfacesResponse;

public interface InterfaceServicePort {
    void createInterface(String token, CreateInterfaceRequest request);

    void deleteInterface(String token, String interfaceId);

    PageResponse<ViewInterfacesResponse> listInterfaces(PageRequest page, String token, String interfaceId, String networkId);

    void allocateExternalIp(String token, String interfaceId);

    void releaseExternalIp(String token, String interfaceId);

    void createSSHForwarding(String token, String interfaceId);

    void deleteSSHForwarding(String token, String interfaceId);
}
