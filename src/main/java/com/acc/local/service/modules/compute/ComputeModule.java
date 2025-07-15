package com.acc.local.service.modules.compute;

import com.acc.local.dto.compute.ComputeDetailResponse;
import com.acc.local.dto.compute.ComputeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import org.springframework.core.ParameterizedTypeReference;

@Component
@RequiredArgsConstructor
public class ComputeModule {
    private final WebClient computeWebClient;

    private <T> List<T> fetchComputeList(String token, String uri, ParameterizedTypeReference<ComputeListWrapper<T>> typeRef) {
        ComputeListWrapper<T> wrapper = computeWebClient.get()
                .uri(uri)
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(typeRef)
                .block();

        return wrapper != null ? wrapper.servers() : List.of();
    }

    public List<ComputeResponse> getComputes(String token) {
        return fetchComputeList(token, "/compute/v2.1/servers",
                new ParameterizedTypeReference<ComputeListWrapper<ComputeResponse>>() {});
    }

    public List<ComputeDetailResponse> getComputeDetail(String token) {
        return fetchComputeList(token, "/compute/v2.1/servers/detail",
                new ParameterizedTypeReference<ComputeListWrapper<ComputeDetailResponse>>() {});
    }
}

