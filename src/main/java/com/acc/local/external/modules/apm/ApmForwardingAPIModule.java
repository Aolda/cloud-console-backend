package com.acc.local.external.modules.apm;

import com.acc.local.external.dto.apm.CreateForwardingRequest;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApmForwardingAPIModule {

    private final ApmAPIModule apmAPIModule;
    private final String uri = "/api/forwarding";

    private static final String CB_NAME = "cb-apm";
    private static final String RETRY_READ = "retry-apm-read";
    private static final String RETRY_WRITE = "retry-apm-write";

    @Retry(name = RETRY_WRITE)
    @CircuitBreaker(name = RETRY_WRITE, fallbackMethod = "fallbackCreateForwarding")
    public ResponseEntity<JsonNode> createForwarding(String token, String projectId, CreateForwardingRequest request) {
        return apmAPIModule.callPostAPI(uri,
                Collections.singletonMap("X-Subject-Token", token),
                Collections.singletonMap("projectId", projectId), request);
    }

    @Retry(name = RETRY_WRITE)
    @CircuitBreaker(name = RETRY_WRITE, fallbackMethod = "fallbackDeleteForwarding")
    public ResponseEntity<JsonNode> deleteForwarding(String token, String forwardingId) {
        return apmAPIModule.callDeleteAPI(uri, Collections.singletonMap("X-Subject-Token", token),
                Collections.singletonMap("forwardingId", forwardingId));
    }

    @Retry(name = RETRY_READ)
    @CircuitBreaker(name = RETRY_WRITE, fallbackMethod = "fallbackListForwarding")
    public ResponseEntity<JsonNode> listForwarding(String token, Map<String, String> queryParams) {
        return apmAPIModule.callGetAPI(uri + 's', Collections.singletonMap("X-Subject-Token", token), queryParams);
    }

    // 오로지 CB-STATE=OPEN 상황만을 로깅함.
    private ResponseEntity<JsonNode> fail(String operation, Throwable t) {
        if (t instanceof CallNotPermittedException e) {
            log.warn("[CB-OPEN] component=APM op={} cb={} ex={} msg={}",
                    operation, CB_NAME,
                    e.getClass().getSimpleName(), e.getMessage(), e);

            throw e;
        }
        // 현재 external.adaptor 계층에서 로깅 하므로 따로 모아서 로깅하지 않음
//        log.warn("[APM-CALL-FAIL] component=APM op={} cb={} ex={} msg={}",
//                operation, CB_NAME,
//                t.getClass().getSimpleName(), t.getMessage(), t);
        //
        throw sneakyThrow(t);
    }

    //원본 에러 그대로 던지기
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }

    private ResponseEntity<JsonNode> fallbackCreateForwarding(String token, String projectId, CreateForwardingRequest request, Throwable t) {
        return fail("createForwarding", t);
    }

    private ResponseEntity<JsonNode> fallbackDeleteForwarding(String token, String forwardingId, Throwable t) {
        return fail("deleteForwarding", t);
    }

    private ResponseEntity<JsonNode> fallbackListForwarding(String token, Map<String, String> queryParams, Throwable t) {
        return fail("listForwarding", t);
    }
}
