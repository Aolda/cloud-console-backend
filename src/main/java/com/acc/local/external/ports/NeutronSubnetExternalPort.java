package com.acc.local.external.ports;

import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.CreateSubnetRequest;
import com.acc.local.dto.network.ViewSubnetsResponse;

import java.util.List;
import java.util.Map;

public interface NeutronSubnetExternalPort {

    List<Map<String, String>> callCreateSubnet(String keystoneToken, List<CreateSubnetRequest> subnets, String networkId);
    void callDeleteSubnet(String keystoneToken, String subnetId);
    PageResponse<ViewSubnetsResponse> callListSubnets(String keystoneToken, String networkId, String marker, String direction, int limit);
    ViewSubnetsResponse getSubnetDetails(String keystoneToken, String subnetId);
}
