package com.acc.local.external.modules.apm;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApmAPIModule {

    private final WebClient apmWebClient;

    public ResponseEntity<JsonNode> callGetAPI(String uri, Map<String, String> headers, Map<String, String> queryParams) {
        return apmWebClient.get()
                .uri(uriBuilder -> {
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(uri).build();
                })
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    public ResponseEntity<JsonNode> callPostAPI(String uri, Map<String, String> headers, Map<String, String> queryParams, Object requestBody) {
        return apmWebClient.post()
                .uri(uriBuilder -> {
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(uri).build();
                })
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    public ResponseEntity<JsonNode> callDeleteAPI(String uri, Map<String, String> headers, Map<String, String> queryParams) {
        return apmWebClient.delete()
                .uri(uriBuilder -> {
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(uri).build();
                })
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    private ResponseEntity<JsonNode> callPatchAPI(String uri, Map<String, String> headers, Map<String, String> queryParams, Object requestBody) {
        return apmWebClient.patch()
                .uri(uriBuilder -> {
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(uri).build();
                })
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }
}
