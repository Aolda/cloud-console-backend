package com.acc.local.service.modules.volume;

import com.acc.local.service.modules.volume.dto.response.CinderVolumeDetailResponse;
import com.acc.local.service.modules.volume.dto.response.CinderVolumeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
public class CinderFetcher {
    private final WebClient cinderWebClient;

    public CinderVolumeResponse fetchVolumes(String token) {
        return requestGet(token, "/v3/volumes", CinderVolumeResponse.class);
    }

    public CinderVolumeDetailResponse fetchVolumesDetail(String token) {
        return requestGet(token, "/v3/volumes/detail", CinderVolumeDetailResponse.class);
    }

    private <T> T requestGet(String token, String url, Class<T> responseType) {
        try {
            return cinderWebClient.get()
                    .uri(url)
                    .header("X-Auth-Token", token)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Cinder API 응답 에러: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Cinder API 요청 중 예외 발생", e);
        }
    }
}