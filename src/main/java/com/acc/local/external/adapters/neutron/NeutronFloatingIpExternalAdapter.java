package com.acc.local.external.adapters.neutron;

import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.external.dto.neutron.floatingips.CreateFloatingIpRequest;
import com.acc.local.external.dto.neutron.floatingips.UpdateFloatingIpRequest;
import com.acc.local.external.modules.neutron.NeutronFloatingIpsAPIModule;
import com.acc.local.external.ports.NeutronFloatingIpExternalPort;
import com.acc.local.external.dto.neutron.response.NeutronFloatingIpsResponse;
import com.acc.local.external.dto.neutron.response.NeutronFloatingIpsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeutronFloatingIpExternalAdapter implements NeutronFloatingIpExternalPort {

    private final NeutronFloatingIpsAPIModule floatingIpsAPIModule;

    @Override
    public void allocateFloatingIpToPort(String keystoneToken, String floatingNetworkId, String portId) {

        try {
            ResponseEntity<?> response = floatingIpsAPIModule.createFloatingIp(keystoneToken,
                    CreateFloatingIpRequest.builder().floatingip(
                            CreateFloatingIpRequest.FloatingIp.builder()
                                    .floatingNetworkId(floatingNetworkId)
                                    .portId(portId)
                                    .build()
                    ).build());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_CREATION_FAILED);
            }
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_BAD_REQUEST, e);
                case 403 ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_FORBIDDEN, e);
                case 404 ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_NOT_FOUND, e);
                default ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_CREATION_FAILED, e);
            }
        }
    }

    @Override
    public Map<String, String> getFloatingIpInfo(String keystoneToken, String portId) {
        try {
            ResponseEntity<NeutronFloatingIpsResponse> response = floatingIpsAPIModule.listFloatingIps(
                    keystoneToken,
                    portId != null ? Map.of("port_id", portId) : null
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_RETRIEVAL_FAILED);
            }

            if (response.getBody().getFloatingips() == null || response.getBody().getFloatingips().isEmpty()) {
                return null;
            }

            var floatingIp = response.getBody().getFloatingips().get(0);
            return Map.of(
                    "id", floatingIp.getId(),
                    "floating_ip_address", floatingIp.getFloatingIpAddress()
            );
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_BAD_REQUEST, e);
                case 403 ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_FORBIDDEN, e);
                case 404 ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_NOT_FOUND, e);
                default ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_RETRIEVAL_FAILED, e);
            }

        }
    }

    @Override
    public void releaseFloatingIpFromPort(String keystoneToken, String floatingIpId) {
        try {
            ResponseEntity<Void> response = floatingIpsAPIModule.deleteFloatingIp(keystoneToken, floatingIpId);
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            switch (e.getStatusCode().value()) {
                case 400 ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_BAD_REQUEST, e);
                case 403 ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_FORBIDDEN, e);
                case 404 ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_NOT_FOUND, e);
                default ->
                        throw new NeutronException(NeutronErrorCode.NEUTRON_FLOATING_IP_RELEASE_FAILED, e);
            }
        }
    }
}
