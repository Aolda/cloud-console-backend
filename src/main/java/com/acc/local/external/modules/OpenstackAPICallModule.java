package com.acc.local.external.modules;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
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
    private final OpenstackResilienceExecutor openstackResilienceExecutor;

    public ResponseEntity<JsonNode> callGetAPI(String uri, Map<String, String> headers, Map<String, String> queryParams, int port) {
        ResponseEntity<JsonNode> response = openstackResilienceExecutor.execute("get", port, () ->
                openstackWebClient.get()
                        .uri(uriBuilder -> {
                            uriBuilder.port(port);
                            if (queryParams != null) {
                                queryParams.forEach(uriBuilder::queryParam);
                            }
                            return uriBuilder.path(uri).build();
                        })
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .retrieve()
                        .toEntity(JsonNode.class)
                        .block()
        );
        return response;
    }

    public ResponseEntity<JsonNode> callGetAPI(String uri, Map<String, String> headers, MultiValueMap<String, String> queryParams, int port) {
        ResponseEntity<JsonNode> response = openstackResilienceExecutor.execute("get", port, () ->
                openstackWebClient.get()
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
                        .block()
        );
        return response;
    }

    public ResponseEntity<JsonNode> callPostAPI(String uri, Map<String, String> headers, Object requestBody, int port) {
        ResponseEntity<JsonNode> response = openstackResilienceExecutor.execute("post", port, () ->
                openstackWebClient.post()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .bodyValue(requestBody)
                        .retrieve()
                        .toEntity(JsonNode.class)
                        .block()
        );
        return response;
    }

    public ResponseEntity<JsonNode> callPutAPI(String uri, Map<String, String> headers, Object requestBody, int port) {
        ResponseEntity<JsonNode> response = openstackResilienceExecutor.execute("put", port, () ->
                openstackWebClient.put()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .bodyValue(requestBody)
                        .retrieve()
                        .toEntity(JsonNode.class)
                        .block()
        );
        return response;
    }

    public ResponseEntity<JsonNode> callDeleteAPI(String uri, Map<String, String> headers, int port) {
        return openstackResilienceExecutor.execute("delete", port, () ->
                openstackWebClient.delete()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .retrieve()
                        .toBodilessEntity()
                        .map(response -> ResponseEntity.status(response.getStatusCode()).body((JsonNode) null))
                        .block()
        );
    }

    public ResponseEntity<JsonNode> callPatchAPI(String uri, Map<String, String> headers, Object requestBody, int port) {
        ResponseEntity<JsonNode> response = openstackResilienceExecutor.execute("patch", port, () ->
                openstackWebClient.patch()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .bodyValue(requestBody)
                        .retrieve()
                        .toEntity(JsonNode.class)
                        .block()
        );
        return response;
    }

    public ResponseEntity<JsonNode> callHeadAPI(String uri, Map<String, String> headers, int port) {
        return openstackResilienceExecutor.execute("head", port, () ->
                openstackWebClient.head()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .retrieve()
                        .toEntity(JsonNode.class)
                        .block()
        );
    }


    public ResponseEntity<JsonNode> callOptionsAPI(String uri, Map<String, String> headers, int port) {
        return openstackResilienceExecutor.execute("options", port, () ->
                openstackWebClient.options()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .retrieve()
                        .toEntity(JsonNode.class)
                        .block()
        );
    }

    public ResponseEntity<Void> callPutBinaryStreamAPI(String uri, Map<String, String> headers, InputStreamResource resource, String contentType, int port) {
        return openstackResilienceExecutor.execute("put", port, () ->
                openstackWebClient.put()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(BodyInserters.fromResource(resource))
                        .retrieve()
                        .toBodilessEntity()
                        .block()
        );
    }

    public ResponseEntity<Void> callPostAPINoBody(String uri, Map<String, String> headers, Object requestBody, int port) {
        return openstackResilienceExecutor.execute("post", port, () ->
                openstackWebClient.post()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .bodyValue(requestBody)
                        .retrieve()
                        .toBodilessEntity()
                        .block()
        );
    }

    public ResponseEntity<Void> callDeleteAPINoBody(String uri, Map<String, String> headers, int port) {
        return openstackResilienceExecutor.execute("delete", port, () ->
                openstackWebClient.delete()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .retrieve()
                        .toBodilessEntity()
                        .block()
        );
    }

    // Generic DTO overloads for typed deserialization
    public <T> ResponseEntity<T> callGetAPI(String uri, Map<String, String> headers, Map<String, String> queryParams, int port, Class<T> responseType) {
        return openstackResilienceExecutor.execute("get", port, () ->
                openstackWebClient.get()
                        .uri(uriBuilder -> {
                            uriBuilder.port(port);
                            if (queryParams != null) {
                                queryParams.forEach(uriBuilder::queryParam);
                            }
                            return uriBuilder.path(uri).build();
                        })
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .retrieve()
                        .toEntity(responseType)
                        .block()
        );
    }

    public <T> ResponseEntity<T> callGetAPI(String uri, Map<String, String> headers, MultiValueMap<String, String> queryParams, int port, Class<T> responseType) {
        return openstackResilienceExecutor.execute("get", port, () ->
                openstackWebClient.get()
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
                        .toEntity(responseType)
                        .block()
        );
    }

    public <T> ResponseEntity<T> callPostAPI(String uri, Map<String, String> headers, Object requestBody, int port, Class<T> responseType) {
        return openstackResilienceExecutor.execute("post", port, () ->
                openstackWebClient.post()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .bodyValue(requestBody)
                        .retrieve()
                        .toEntity(responseType)
                        .block()
        );
    }

    public <T> ResponseEntity<T> callPutAPI(String uri, Map<String, String> headers, Object requestBody, int port, Class<T> responseType) {
        return openstackResilienceExecutor.execute("put", port, () ->
                openstackWebClient.put()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .bodyValue(requestBody)
                        .retrieve()
                        .toEntity(responseType)
                        .block()
        );
    }

    public <T> ResponseEntity<T> callPatchAPI(String uri, Map<String, String> headers, Object requestBody, int port, Class<T> responseType) {
        return openstackResilienceExecutor.execute("patch", port, () ->
                openstackWebClient.patch()
                        .uri(uriBuilder -> uriBuilder.port(port).path(uri).build())
                        .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                        .bodyValue(requestBody)
                        .retrieve()
                        .toEntity(responseType)
                        .block()
        );
    }
}
