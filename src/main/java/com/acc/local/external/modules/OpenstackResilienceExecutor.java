package com.acc.local.external.modules;

import com.acc.global.exception.common.CommonErrorCode;
import com.acc.global.exception.common.ServiceUnavailableException;
import com.acc.global.properties.OpenstackComponentProperties;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenstackResilienceExecutor {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final OpenstackComponentProperties openstackComponentProperties;

    private final Set<String> hookedRetry = ConcurrentHashMap.newKeySet();
    private final Set<String> hookedCb = ConcurrentHashMap.newKeySet();

    /**
     * @param httpMethod ex) "get", "post", "put", "delete", "patch", "head", "options"
     * @param port OpenStack component port (keystone 5000, nova 8774, cinder 8776, glance 9292, neutron 9696)
     */
    public <T> T execute(String httpMethod, int port, Supplier<T> supplier) {
        String method = normalizeMethod(httpMethod);
        String component = resolveComponentByPort(port);

        String cbName = "cb-" + component;
        String retryName = "retry-" + component + "-" + method;

        CircuitBreaker cb = getCircuitBreakerOrDefault(cbName);
        Retry retry = getRetryOrDefault(retryName, "retry-default-" + method);

        Supplier<T> decorated = CircuitBreaker.decorateSupplier(
                cb,
                Retry.decorateSupplier(retry, supplier)
        );

        try {
            return decorated.get();
        } catch (CallNotPermittedException e) {
            // CB OPEN fallback: "차단"은 resilience4j로 인해 발생하는 인프라 일시 통합 장애 상황이므로 임시로 장애 분리
            log.warn("[CB-OPEN] component={} port={} method={} cb={} state={}",
                    component, port, method, cb.getName(), cb.getState(), e);
            throw new ServiceUnavailableException(CommonErrorCode.OPENSTACK_INFRA_UNAVAILABLE, component+ " 임시 오류");
        } catch (Exception e) {
            // supplier 의 에러가 정책 상 무시되지 않는 타입이라면 retry 횟수가 전부 소진된 후에 1회 던져짐.
            // 장애 판단을 상위에서 각자하고 그대로 throw + 얇은 로깅만 구현 (추후 로그 레벨 분리 및 메트릭 수집 기능 필요)
            log.warn("[OPENSTACK-CALL-FAIL] component={} port={} method={} cb={} state={} ex={} msg={}",
                    component, port, method, cb.getName(), cb.getState(),
                    e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

    private String normalizeMethod(String httpMethod) {
        if (httpMethod == null || httpMethod.isBlank()) return "get";
        return httpMethod.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * 최소 초안: port 기반 분기.
     */
    private String resolveComponentByPort(int port) {
        return openstackComponentProperties.getComponents().entrySet().stream()
                .filter(entry -> entry.getValue().getPort() == port)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(openstackComponentProperties.getResilience().getDefaultComponent());
    }


    private CircuitBreaker getCircuitBreakerOrDefault(String cbName) {
        // yml에 없으면 cb-default로 fallback
        CircuitBreaker cb;
        try {
            cb = circuitBreakerRegistry.circuitBreaker(cbName);
        } catch (Exception ignored) {
            cb = circuitBreakerRegistry.circuitBreaker("cb-default");
        }
        return hookCircuitBreaker(cb);
    }

    private Retry getRetryOrDefault(String primaryRetryName, String fallbackRetryName) {
        // yml에 없으면 retry-default-{method}로 fallback
        Retry retry;
        try {
            retry = retryRegistry.retry(primaryRetryName);
        } catch (Exception ignored) {
            retry = retryRegistry.retry(fallbackRetryName);
        }
        return hookRetry(retry);
    }

    private CircuitBreaker hookCircuitBreaker(CircuitBreaker cb) {
        if (!hookedCb.add(cb.getName())) {
            return cb;
        }

        cb.getEventPublisher()
                .onStateTransition(event -> {
                    CircuitBreakerOnStateTransitionEvent e = (CircuitBreakerOnStateTransitionEvent) event;
                    log.warn("[CB-STATE] name={} {} -> {}",
                            e.getCircuitBreakerName(),
                            e.getStateTransition().getFromState(),
                            e.getStateTransition().getToState());
                })
                .onError(event -> log.warn("[CB-ERROR] name={} ex={} msg={}",
                        event.getCircuitBreakerName(),
                        event.getThrowable().getClass().getSimpleName(),
                        event.getThrowable().getMessage())
                );

        log.info("[CB-HOOKED] {}", cb.getName());
        return cb;
    }

    private Retry hookRetry(Retry retry) {
        if (!hookedRetry.add(retry.getName())) {
            return retry; // 이미 리스너 붙음
        }

        retry.getEventPublisher()
                .onRetry(event -> {
                    Throwable last = event.getLastThrowable();
                    log.warn("[RETRY] name={} attempt={} lastEx={} msg={}",
                            event.getName(),
                            event.getNumberOfRetryAttempts(),
                            last != null ? last.getClass().getSimpleName() : "null",
                            last != null ? last.getMessage() : "null");
                })
                .onSuccess(event -> {
                    log.info("[RETRY-SUCCESS] name={} attempts={}",
                            event.getName(),
                            event.getNumberOfRetryAttempts());
                })
                .onError(event -> {
                    Throwable last = event.getLastThrowable();
                    log.warn("[RETRY-ERROR] name={} attempts={} lastEx={} msg={}",
                            event.getName(),
                            event.getNumberOfRetryAttempts(),
                            last != null ? last.getClass().getSimpleName() : "null",
                            last != null ? last.getMessage() : "null",
                            last);
                });

        log.info("[RETRY-HOOKED] {}", retry.getName());
        return retry;
    }
}
