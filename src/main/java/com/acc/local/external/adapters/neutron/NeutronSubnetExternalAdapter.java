package com.acc.local.external.adapters.neutron;

import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.external.dto.neutron.subnets.BulkCreateSubnetRequest;
import com.acc.local.external.dto.neutron.subnets.CreateSubnetRequest;
import com.acc.local.external.modules.neutron.NeutronSubnetsAPIModule;
import com.acc.local.external.ports.NeutronSubnetExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeutronSubnetExternalAdapter implements NeutronSubnetExternalPort {

    private final NeutronSubnetsAPIModule subnetsAPIModule;

    public List<Map<String, String>> callCreateSubnet(String keystoneToken, List<CreateNetworkRequest.Subnet> subnets, String networkId) {
        try {
            ResponseEntity<JsonNode> response = subnetsAPIModule.bulkCreateSubnets(keystoneToken,
                    BulkCreateSubnetRequest.builder()
                            .subnets(
                                    subnets.stream().map(subnet -> BulkCreateSubnetRequest.Subnet.builder()
                                            .cidr(subnet.getCidr())
                                            .networkId(networkId)
                                            .ipVersion(4)
                                            .name(subnet.getSubnetName())
                                            .build()).toList()
                            )
                            .build()
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_CREATION_FAILED);
            }

            List<Map<String, String>> sub = new ArrayList<>();
            for (JsonNode node : response.getBody().get("subnets")) {
                sub.add(
                        Map.of("id", node.get("id").asText(),
                                "name", node.get("name").asText())
                );
            }
            return sub;
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_BAD_REQUEST, e);
                case 403 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_FORBIDDEN, e);
                case 404 -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_NOT_FOUND, e);
                default -> throw new NeutronException(NeutronErrorCode.NEUTRON_SUBNET_CREATION_FAILED, e);
            }
        }
    }
}
