package com.acc.server.local.service.modules.cinder;

import com.acc.server.local.service.modules.cinder.dto.VolumeDetailResponse;
import com.acc.server.local.service.modules.cinder.dto.VolumeResponse;
import com.acc.server.local.service.modules.keystoneToken.dto.KeystoneTokenResponse;
import com.acc.server.local.service.modules.keystoneToken.dto.KeystoneTokenResponseBody;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class CinderFetcher {
    private final WebClient webClient;
    private final CinderApiProperties properties;
    private final String volumesUrl;
    private final String volumesDetailUrl;

    public CinderFetcher(CinderApiProperties props){
        this.properties = props;
        this.volumesUrl = props.getVolumesUrl();
        this.volumesDetailUrl = props.getVolumesDetailUrl();
        this.webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public Mono<VolumeResponse> fetchVolumes(String token) {
        return webClient.get()
                .uri(volumesUrl)
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(VolumeResponse.class);
    }

    public Mono<VolumeDetailResponse> fetchVolumesDetail(String token) {
        return webClient.get()
                .uri(volumesDetailUrl)
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(VolumeDetailResponse.class);
    }
}
