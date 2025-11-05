package com.acc.local.external.ports;

import com.acc.local.dto.network.CreateNetworkRequest;

import java.util.List;

public interface NeutronSubnetExternalPort {

    void callCreateSubnet(String keystoneToken, List<CreateNetworkRequest.Subnet> subnets, String networkId);
}
