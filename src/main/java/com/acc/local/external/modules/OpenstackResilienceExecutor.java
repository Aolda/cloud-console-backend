package com.acc.local.external.modules;

import com.acc.global.exception.common.CommonErrorCode;
import com.acc.global.exception.common.ServiceUnavailableException;
import com.acc.global.properties.OpenstackComponentProperties;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class OpenstackResilienceExecutor {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final OpenstackComponentProperties openstackComponentProperties;

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
            // CB OPEN fallback: "차단"은 resilience4j로 인해 발생하는 인프라 일시 통합 장애 상황이므로 임시로 장애 분리 (로깅 로직 수정 필요)
            System.out.println("[CB-OPEN] component=" + component + " port=" + port + " method=" + method
                    + " cb=" + cb.getName() + " state=" + cb.getState());
            throw new ServiceUnavailableException(CommonErrorCode.OPENSTACK_INFRA_UNAVAILABLE, component+ " 임시 오류");
        } catch (Exception e) {
            // supplier 의 에러가 정책 상 무시되지 않는 타입이라면 retry 횟수가 전부 소진된 후에 1회 던져짐.
            // supplier 의 모든 로그 수집을 위해선 Resilience4j의 Retry 이벤트 퍼블리셔로 “재시도 발생”으로 구현 필요.
            // 장애 판단을 상위에서 각자하고 그대로 throw + 얇은 로깅만 구현 (추후 로그 레벨 분리 및 메트릭 수집 기능 필요)
            System.out.println("[OPENSTACK-CALL-FAIL] component=" + component + " port=" + port + " method=" + method
                    + " cb=" + cb.getName() + " state=" + cb.getState()
                    + " ex=" + e.getClass().getSimpleName() + " msg=" + e.getMessage());
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
        try {
            return circuitBreakerRegistry.circuitBreaker(cbName);
        } catch (Exception ignored) {
            return circuitBreakerRegistry.circuitBreaker("cb-default");
        }
    }

    private Retry getRetryOrDefault(String primaryRetryName, String fallbackRetryName) {
        // yml에 없으면 retry-default-{method}로 fallback
        try {
            return retryRegistry.retry(primaryRetryName);
        } catch (Exception ignored) {
            return retryRegistry.retry(fallbackRetryName);
        }
    }
}
