package com.acc.local.external.modules;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.core.io.Resource;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenstackAPICallModule {

    private final WebClient openstackWebClient;

    public ResponseEntity<JsonNode> callGetAPI(String uri, Map<String, String> headers, Map<String, String> queryParams, int port) {
        return openstackWebClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.port(port);
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(uri).build();
                })
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    public ResponseEntity<JsonNode> callGetAPI(String uri, Map<String, String> headers, MultiValueMap<String, String> queryParams, int port) {
        return openstackWebClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.port(port);
                    if (queryParams != null) {
                        queryParams.forEach((key, values) -> {
                            if (values != null) {
                                values.forEach(value -> uriBuilder.queryParam(key, value));
                            }
                        });
                    }
                    return uriBuilder.path(uri).build();
                })
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    public ResponseEntity<JsonNode> callPostAPI(String uri, Map<String, String> headers, Object requestBody, int port) {
        return openstackWebClient.post()
                .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    public ResponseEntity<JsonNode> callPutAPI(String uri, Map<String, String> headers, Object requestBody, int port) {
        return openstackWebClient.put()
                .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    public ResponseEntity<JsonNode> callDeleteAPI(String uri, Map<String, String> headers, int port) {
        return openstackWebClient.delete()
                .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    public ResponseEntity<JsonNode> callPatchAPI(String uri, Map<String, String> headers, Object requestBody, int port) {
        return openstackWebClient.patch()
                .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    public ResponseEntity<JsonNode> callHeadAPI(String uri, Map<String, String> headers, int port) {
        return openstackWebClient.head()
                .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }


    public ResponseEntity<JsonNode> callOptionsAPI(String uri, Map<String, String> headers, int port) {
        return openstackWebClient.options()
                .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

    //binary 업로드 함수 임시 구현 추후 논블로킹 업그레이드
    /// chunck 단위로 바로 업로드
    public ResponseEntity<JsonNode> callPutBinaryResourceAPI(
            String uri, Map<String, String> headers, Resource resource, int port) {
        return openstackWebClient.put()
                .uri(b -> b.port(port).path(uri).build())
                .headers(h -> headers.forEach(h::add))
                .header("Content-Type", "application/octet-stream")
                .body(BodyInserters.fromResource(resource))
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }

}

