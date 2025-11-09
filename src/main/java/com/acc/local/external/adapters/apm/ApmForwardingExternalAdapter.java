package com.acc.local.external.adapters.apm;

import com.acc.global.exception.network.ApmErrorCode;
import com.acc.global.exception.network.ApmException;
import com.acc.local.external.dto.apm.CreateForwardingRequest;
import com.acc.local.external.modules.apm.ApmForwardingAPIModule;
import com.acc.local.external.ports.ApmForwardingExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Primary
@Slf4j
public class ApmForwardingExternalAdapter implements ApmForwardingExternalPort {

    private final ApmForwardingAPIModule apmForwardingAPIModule;

    @Override
    public Map<String, String> createSSHForwarding(String keystoneToken, String projectId, String instanceIp, String name) {
        try {
            ResponseEntity<JsonNode> response = apmForwardingAPIModule.createForwarding(keystoneToken,
                    projectId,
                    CreateForwardingRequest.builder()
                            .instanceIp(instanceIp)
                            .instancePort("22")
                            .name(name)
                            .build());

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ApmException(ApmErrorCode.APM_FORWARDING_CREATION_FAILED);
            }

            return Map.of(
                    "id", response.getBody().get("id").asText(),
                    "serverPort", response.getBody().get("serverPort").asText()
            );
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            throw new ApmException(ApmErrorCode.APM_FORWARDING_CREATION_FAILED, e);
        }
    }

    @Override
    public void deleteForwarding(String keystoneToken, String forwardingId) {
        try {
            apmForwardingAPIModule.deleteForwarding(keystoneToken, forwardingId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ApmException(ApmErrorCode.APM_FORWARDING_DELETION_FAILED, e);
        }
    }

    @Override
    public String getForwardingId(String keystoneToken, String projectId, String instanceIp) {
        try {
            ResponseEntity<JsonNode> response = apmForwardingAPIModule.listForwarding(keystoneToken,
                    Map.of("query", instanceIp,
                            "projectId", projectId)
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ApmException(ApmErrorCode.APM_FORWARDING_RETRIEVAL_FAILED);
            }

            if (response.getBody().get("contents").isEmpty()) {
                return null;
            }

            return response.getBody().get("contents").get(0).get("id").asText();
        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e.getResponseBodyAsString(), e);
            throw new ApmException(ApmErrorCode.APM_FORWARDING_RETRIEVAL_FAILED, e);
        }
    }

}
