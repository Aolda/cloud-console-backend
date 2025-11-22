package com.acc.local.external.ports;

import com.acc.local.dto.network.CreateNetworkRequest;

import java.util.List;
import java.util.Map;

public interface NeutronSubnetExternalPort {

    List<Map<String, String>> callCreateSubnet(String keystoneToken, List<CreateNetworkRequest.Subnet> subnets, String networkId);
}
