package com.acc.local.external.modules;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenstackAPICallModule {

    private final WebClient openstackWebClient;

    public JsonNode callGetAPI(String uri, Map<String, String> headers, Map<String, String> queryParams) {
        return openstackWebClient.get()
                .uri(uriBuilder -> {
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.path(uri).build();
                })
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    public JsonNode callPostAPI(String uri, Map<String, String> headers, Object requestBody) {
        return openstackWebClient.post()
                .uri(uri)
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    public JsonNode callPutAPI(String uri, Map<String, String> headers, Object requestBody) {
        return openstackWebClient.put()
                .uri(uri)
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    public JsonNode callDeleteAPI(String uri, Map<String, String> headers) {
        return openstackWebClient.delete()
                .uri(uri)
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    public JsonNode callPatchAPI(String uri, Map<String, String> headers, Object requestBody) {
        return openstackWebClient.patch()
                .uri(uri)
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    public JsonNode callHeadAPI(String uri, Map<String, String> headers) {
        return openstackWebClient.head()
                .uri(uri)
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }


    public JsonNode callOptionsAPI(String uri, Map<String, String> headers) {
        return openstackWebClient.options()
                .uri(uri)
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }


}

