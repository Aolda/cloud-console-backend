package com.acc.local.service.modules.port;

import com.acc.dto.port.*;
import com.acc.global.config.OpenstackConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PortModule {

    private final WebClient webClient;
    private final OpenstackConfig openstackConfig;

    @Value("${openstack.neutron.port-uri:/v2.0/ports}")
    private String portUri;

    public PortResponse createPort(PortRequest request, String token) {
        return webClient.post()
                .uri(openstackConfig.getNeutronEndpoint() + portUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(new PortRequestWrapper(request))
                .retrieve()
                .bodyToMono(PortResponseWrapper.class)
                .block()
                .port();
    }

    public List<PortResponse> listPorts(String token) {
        return webClient.get()
                .uri(openstackConfig.getNeutronEndpoint() + portUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(PortListWrapper.class)
                .block()
                .ports();
    }

    public PortResponse updatePort(String id, PortUpdateRequest request, String token) {
        return webClient.put()
                .uri(openstackConfig.getNeutronEndpoint() + portUri + "/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PortResponseWrapper.class)
                .block()
                .port();
    }

    public void deletePort(String id, String token) {
        webClient.delete()
                .uri(openstackConfig.getNeutronEndpoint() + portUri + "/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}