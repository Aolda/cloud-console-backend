package com.acc.local.dto.network;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ViewInterfacesResponse {

    private String interfaceId;
    private String interfaceName;
    private String status;
    private String internalIp;
    private String externalIp;
    private Instance instance;
    private Network network;
    private String mac;

    @Builder
    @Setter
    @Getter
    public static class Instance {
        private String instanceId;
        private String instanceName;
    }

    @Builder
    @Setter
    @Getter
    public static class Network {
        private String networkId;
        private String networkName;
    }

}
