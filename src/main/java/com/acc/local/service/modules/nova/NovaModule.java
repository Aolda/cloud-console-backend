package com.acc.local.service.modules.nova;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NovaModule {

    @Value("${openstack.nova.endpoint}")
    private String novaEndpoint;

    private final WebClient webClient;

    public String getInstanceName(String instanceId, String token) {
        try {
            Map<String, Object> response = webClient.get()
                .uri(novaEndpoint + "/servers/detail")
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            List<Map<String, Object>> servers = (List<Map<String, Object>>)
                    ((Map<String, Object>) response).get("servers");

            for (Map<String, Object> server : servers) {
                if (server.get("id").equals(instanceId)) {
                    return (String) server.get("name");
                }
            }
        } catch (Exception e) {
            return "(unknown)";
        }
        return "(not found)";
    }
}