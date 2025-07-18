package com.acc.local.service.modules.network;

import com.acc.dto.network.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NetworkModule {

    private final WebClient neutronWebClient;

    public NetworkResponse createNetwork(NetworkRequest request, String token) {
        return neutronWebClient.post()
                .uri("/v2.0/networks")
                .header("X-Auth-Token", token)
                .bodyValue(Map.of("network", request))
                .retrieve()
                .bodyToMono(NetworkResponseWrapper.class)
                .map(NetworkResponseWrapper::network)
                .block();
    }

    public List<NetworkResponse> listNetworks(String token) {
        return neutronWebClient.get()
                .uri("/v2.0/networks")
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(NetworkListWrapper.class)
                .map(NetworkListWrapper::networks)
                .block();
    }

    public NetworkResponse updateNetwork(String id, NetworkUpdateRequest request, String token) {
        return neutronWebClient.put()
                .uri("/v2.0/networks/" + id)
                .header("X-Auth-Token", token)
                .bodyValue(Map.of("network", request))
                .retrieve()
                .bodyToMono(NetworkResponseWrapper.class)
                .map(NetworkResponseWrapper::network)
                .block();
    }

    public void deleteNetwork(String id, String token) {
        neutronWebClient.delete()
                .uri("/v2.0/networks/" + id)
                .header("X-Auth-Token", token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}