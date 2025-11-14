package com.acc.local.external.adapters.neutron;

import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.external.dto.neutron.floatingips.CreateFloatingIpRequest;
import com.acc.local.external.dto.neutron.floatingips.UpdateFloatingIpRequest;
import com.acc.local.external.modules.neutron.NeutronFloatingIpsAPIModule;
import com.acc.local.external.ports.NeutronFloatingIpExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeutronFloatingIpExternalAdapter implements NeutronFloatingIpExternalPort {

    private final NeutronFloatingIpsAPIModule floatingIpsAPIModule;

    @Override
    public void allocateFloatingIpToPort(String keystoneToken, String floatingNetworkId, String portId) {

        try {
            ResponseEntity<JsonNode> response = floatingIpsAPIModule.createFloatingIp(keystoneToken,
                    CreateFloatingIpRequest.builder().floatingip(
                            CreateFloatingIpRequest.FloatingIp.builder()
                                    .floatingNetworkId(floatingNetworkId)
                                    .portId(portId)
                                    .build()
                    ).build());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_CREATION_FAILED);
            }
        } catch (WebClientException e) {
            log.error(e.getMessage(), e);
            throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_CREATION_FAILED, e);
        }
    }

    @Override
    public Map<String, String> getFloatingIpInfo(String keystoneToken, String portId) {
        try {
            ResponseEntity<JsonNode> response = floatingIpsAPIModule.listFloatingIps(
                    keystoneToken,
                    portId != null ? Map.of("port_id", portId) : null
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_RETRIEVAL_FAILED);
            }

            if (response.getBody().get("floatingips").isEmpty()) {
                return null;
            }

            JsonNode floatingIpNode = response.getBody().get("floatingips").get(0);
            return Map.of(
                    "id", floatingIpNode.get("id").asText(),
                    "floating_ip_address", floatingIpNode.get("floating_ip_address").asText()
            );
        } catch (WebClientException e) {
            log.error(e.getMessage(), e);
            throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_RETRIEVAL_FAILED, e);
        }
    }

    @Override
    public void releaseFloatingIpFromPort(String keystoneToken, String floatingIpId) {
        try {
            ResponseEntity<JsonNode> response = floatingIpsAPIModule.deleteFloatingIp(keystoneToken, floatingIpId);
        } catch (WebClientException e) {
            log.error(e.getMessage(), e);
            throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_RELEASE_FAILED, e);
        }
    }
}
