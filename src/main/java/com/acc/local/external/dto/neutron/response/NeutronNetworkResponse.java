package com.acc.local.external.dto.neutron.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeutronNetworkResponse {

    private NeutronNetworksResponse.Network network;
}