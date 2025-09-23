package com.acc.local.service.modules.flavor;

import com.acc.local.dto.flavor.FlavorDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class FlavorModule {

    private final WebClient computeWebClient;

    public List<FlavorDto> getAllFlavors(String token) {
        JsonNode listResponse = computeWebClient.get()
                .uri("/compute/v2.1/flavors")
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<FlavorDto> flavors = new ArrayList<>();
        for (JsonNode flavorSummary : listResponse.path("flavors")) {
            String id = flavorSummary.path("id").asText();
            flavors.add(getFlavorById(token, id));
        }

        return flavors;
    }

    public FlavorDto getFlavorById(String token, String id) {
        JsonNode flavorDetail = computeWebClient.get()
                .uri("/compute/v2.1/flavors/{id}", id)
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block()
                .path("flavor");

        return parseFlavorDto(flavorDetail);
    }

    private FlavorDto parseFlavorDto(JsonNode node) {
        return new FlavorDto(
                node.path("id").asText(),
                node.path("name").asText(),
                node.path("vcpus").asInt(),
                node.path("ram").asInt(),
                node.path("disk").asInt(),
                node.path("OS-FLV-EXT-DATA:ephemeral").asInt(0),
                node.path("rxtx_factor").asDouble(1.0),
                node.path("os-flavor-access:is_public").asBoolean(false)
        );
    }
}
