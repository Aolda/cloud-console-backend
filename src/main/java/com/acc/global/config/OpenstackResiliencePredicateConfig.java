package com.acc.global.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.Predicate;

@Slf4j
@Configuration
public class OpenstackResiliencePredicateConfig {

    private static final String CB_DEFAULT_CONFIG = "default-config";
    private static final String RETRY_DEFAULT_GET_CONFIG = "default-get";

    /**
     * CircuitBreaker: 블랙리스트 전략
     * 적용 대상: configs.default-config
     *  - instances.* 는 baseConfig로 default-config를 참조하므로 전부 동일 정책 적용됨
     */
    @Bean
    public CircuitBreakerConfigCustomizer openstackCircuitBreakerCustomizer() {
        return CircuitBreakerConfigCustomizer.of(CB_DEFAULT_CONFIG, this::customizeCircuitBreakerDefault);
    }

    private void customizeCircuitBreakerDefault(CircuitBreakerConfig.Builder builder) {
        // HTTP 응답 중 "무시할 것"을 ignore로 정의 (블랙리스트)
        builder.ignoreException(ignoreHttpForCircuitBreaker());

//        // 정책 적용 확인 로그
//        log.info("[RESILIENCE-PREDICATE][CB] config={} policy=ignore 4xx, ignore non-500/502/503/504 5xx",
//                CB_DEFAULT_CONFIG);
    }

    /**
     * Retry: 블랙리스트 전략 (기본은 retry)
     *  적용 대상: configs.default-get
     * - instances.*-get 은 baseConfig로 default-get를 참조하므로 전부 동일 정책 적용됨
     */
    @Bean
    public RetryConfigCustomizer openstackRetryGetCustomizer() {
        return RetryConfigCustomizer.of(RETRY_DEFAULT_GET_CONFIG, this::customizeRetryDefaultGet);
    }

    private void customizeRetryDefaultGet(RetryConfig.Builder builder) {
        builder.retryOnException(retryOnHttpForRetry());
//        // 정책 적용 확인 로그
//        log.info("[RESILIENCE-PREDICATE][RETRY] config={} policy=retry only 500/502/503/504 for 5xx; never retry 4xx",
//                RETRY_DEFAULT_GET_CONFIG);
    }

    /**
     * CircuitBreaker ignore predicate:
     * - return true  => ignore (failure 기록 X)
     * - return false => record 대상으로 남김
     */
    private Predicate<Throwable> ignoreHttpForCircuitBreaker() {
        return t -> {
            if (!(t instanceof WebClientResponseException wcre)) return false;

            var sc = wcre.getStatusCode();
            if (sc.is4xxClientError()) return true;
            if (sc.is5xxServerError()) return (!shouldRecord5xx(sc.value()));
            return false;
        };
    }

    /**
     * Retry predicate:
     * - return true  => retry 한다
     * - return false => retry 안 한다
     * - HTTP 응답이 아닌 예외는 기본 true
     */
    private Predicate<Throwable> retryOnHttpForRetry() {
        return t -> {
            if (!(t instanceof WebClientResponseException wcre)) {
                // HTTP 응답 아닌 예외는 retry 허용(블랙리스트 전략)
                return true;
            }
            var sc = wcre.getStatusCode();
            if (sc.is4xxClientError()) return false;
            if (sc.is5xxServerError()) return shouldRecord5xx(sc.value());
            return false;
        };
    }

    private boolean shouldRecord5xx(int code) {
        return code == 500 || code == 502 || code == 503 || code == 504;
    }
}
