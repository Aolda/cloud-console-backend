package com.acc.server.local.service.modules.cinder;

import com.acc.server.global.properties.external.CinderProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.acc.server.local.service.modules.cinder.dto.VolumeDetailResponse;
import com.acc.server.local.service.modules.cinder.dto.VolumeResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class CinderFetcher {
    private final WebClient webClient;
    private final String volumesUrl;
    private final String volumesDetailUrl;

    public CinderFetcher(CinderProperties props) {
        this.volumesUrl = props.getVolumesUrl();
        this.volumesDetailUrl = props.getVolumesDetailUrl();
        this.webClient = WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .build();
    }

    public Mono<VolumeResponse> fetchVolumes(String token) {
        return requestGet(token, volumesUrl, VolumeResponse.class);
    }

    public Mono<VolumeDetailResponse> fetchVolumesDetail(String token) {
        return requestGet(token, volumesDetailUrl, VolumeDetailResponse.class);
    }

    private <T> Mono<T> requestGet(String token, String url, Class<T> responseType) {
        try {
            return webClient.get()
                    .uri(url)
                    .header("X-Auth-Token", token)
                    .retrieve()
                    .bodyToMono(responseType);
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Cinder API 응답 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Cinder API 요청 중 예외 발생", e);
        }
    }
}
