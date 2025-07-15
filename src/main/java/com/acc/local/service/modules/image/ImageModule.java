package com.acc.local.service.modules.image;

import com.acc.local.dto.image.ImageResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageModule {

    private final WebClient glanceWebClient;
    public List<ImageResponse> getImages(String token) {
        return glanceWebClient.get()
                .uri("/image/v2/images")
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> {
                    List<ImageResponse> list = new ArrayList<>();
                    root.get("images").forEach(node -> list.add(parseImage(node)));
                    return list;
                })
                .block();
    }

    public ImageResponse getImageDetail(String token, String imageId) {
        return glanceWebClient.get()
                .uri("/image/v2/images/{id}", imageId)
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::parseImage)
                .block();
    }

    private ImageResponse parseImage(JsonNode node) {
        return new ImageResponse(
                node.get("id").asText(),
                node.get("name").asText(),
                node.get("status").asText(),
                node.get("visibility").asText(),
                node.hasNonNull("size") ? node.get("size").asLong() : null,
                node.get("disk_format").asText(),
                node.get("container_format").asText(),
                node.get("created_at").asText(),
                node.get("updated_at").asText()
        );
    }
}
