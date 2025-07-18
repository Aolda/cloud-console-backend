package com.acc.local.service.modules.subnet;

import com.acc.dto.subnet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubnetModule {

    private final WebClient neutronWebClient;

    public SubnetResponse createSubnet(SubnetRequest request, String token) {
        return neutronWebClient.post()
                .uri("/v2.0/subnets")
                .header("X-Auth-Token", token)
                .bodyValue(Map.of("subnet", request))
                .retrieve()
                .bodyToMono(SubnetResponseWrapper.class)
                .map(SubnetResponseWrapper::subnet)
                .block();
    }

    public List<SubnetResponse> listSubnets(String token) {
        return neutronWebClient.get()
                .uri("/v2.0/subnets")
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(SubnetListWrapper.class)
                .map(SubnetListWrapper::subnets)
                .block();
    }

    public SubnetResponse updateSubnet(String id, SubnetUpdateRequest request, String token) {
        return neutronWebClient.put()
                .uri("/v2.0/subnets/" + id)
                .header("X-Auth-Token", token)
                .bodyValue(Map.of("subnet", request))
                .retrieve()
                .bodyToMono(SubnetResponseWrapper.class)
                .map(SubnetResponseWrapper::subnet)
                .block();
    }

    public void deleteSubnet(String id, String token) {
        neutronWebClient.delete()
                .uri("/v2.0/subnets/" + id)
                .header("X-Auth-Token", token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
