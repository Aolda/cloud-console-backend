package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateSubnetRequest;
import com.acc.local.dto.network.ViewSubnetsResponse;

public interface SubnetServicePort {
    void createSubnet(CreateSubnetRequest request, String networkId, String projectId, String sessionId);
    void deleteSubnet(String subnetId, String projectId, String sessionId);
    PageResponse<ViewSubnetsResponse> listSubnets(PageRequest page, String networkId, String projectId, String sessionId);
    ViewSubnetsResponse getSubnetDetail(String subnetId, String projectId, String sessionId);
}
