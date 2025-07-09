package com.acc.server.local.service.modules.cinder;

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

    public CinderFetcher(
            @Value("${cinder.base-url}") String baseUrl,
            @Value("${cinder.volumes-url}") String volumesUrl,
            @Value("${cinder.volumes-detail-url}") String volumesDetailUrl
    ) {
        this.volumesUrl = volumesUrl;
        this.volumesDetailUrl = volumesDetailUrl;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
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
