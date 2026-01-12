package com.acc.global.logging.aspect;

import com.acc.global.logging.enums.SystemType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static net.logstash.logback.argument.StructuredArguments.raw;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ExternalApiLoggingAspect {

    private final ObjectMapper objectMapper;

    @Pointcut("execution(* com.acc.local.external.modules.*.*.*(..))")
    public void externalModuleLayer() {}

    @Around("externalModuleLayer()")
    public Object logExternalCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        String targetSystem = String.valueOf(SystemType.findByName(className.split("(?=[A-Z])")[0]));

        MDC.put("type", "EXTERNAL");
        MDC.put("targetSystem", targetSystem);
        MDC.put("module", className);
        MDC.put("method", methodName);
        MDC.put("attempt", "1");
        
        String argsJson = sanitizeArgs(joinPoint.getArgs(), signature.getParameterNames());
        Object argsArg = raw("args", argsJson);

        try {
            Object result = joinPoint.proceed();
            
            boolean isSuccess = true;
            if (result instanceof ResponseEntity<?> response) {
                MDC.put("statusCode", String.valueOf(response.getStatusCode().value()));
                if (response.getStatusCode().is4xxClientError()) {
                    isSuccess = false;
                    MDC.put("success", String.valueOf(isSuccess));
                    log.warn("[External] {} Client Error - {} {}", targetSystem, className, methodName, argsArg);
                } else if (response.getStatusCode().is5xxServerError()) {
                    isSuccess = false;
                    MDC.put("success", String.valueOf(isSuccess));
                    log.error("[External] {} Server Error - {} {}", targetSystem, className, methodName, argsArg);
                }
            }


            MDC.put("success", String.valueOf(isSuccess));
            if (isSuccess) {
                log.info("[External] {} Call Success - {} {}", targetSystem, className, methodName, argsArg);
            }
            return result;

        } catch (Throwable ex) {
            MDC.put("success", "false");
            MDC.put("exception", ex.getClass().getSimpleName());
            MDC.put("errorMessage", ex.getMessage());

            if (ex instanceof WebClientResponseException webEx) {
                MDC.put("statusCode", String.valueOf(webEx.getStatusCode().value()));
                
                Object responseBodyArg = getResponseBodyArg(webEx.getResponseBodyAsString());
                
                if (webEx.getStatusCode().is4xxClientError()) {
                    log.warn("[External] {} Client Exception - {} {}", targetSystem, className, methodName, responseBodyArg, argsArg);
                } else {
                    log.error("[External] {} System Exception - {} {}", targetSystem, className, methodName, responseBodyArg, argsArg);
                }
            } else {
                log.error("[External] {} Unknown Exception - {} {}", targetSystem, className, methodName, argsArg);
            }
            throw ex;
        } finally {
            MDC.put("durationMs", String.valueOf(System.currentTimeMillis() - startTime));
            cleanupMdcKeys("type", "targetSystem", "module", "method", "statusCode", "success", "exception", "errorMessage", "durationMs", "attempt");
        }
    }

    private String sanitizeArgs(Object[] args, String[] paramNames) {
        try {
            Map<String, Object> safeArgs = new HashMap<>();
            if (paramNames != null && args != null && args.length == paramNames.length) {
                for (int i = 0; i < args.length; i++) {
                    String paramName = paramNames[i];
                    Object argValue = args[i];
                    
                    if (isSensitive(paramName)) {
                        safeArgs.put(paramName, "***");
                    } else {
                        safeArgs.put(paramName, argValue);
                    }
                }
            }
            return objectMapper.writeValueAsString(safeArgs);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Serialization Failed\"}";
        }
    }

    private Object getResponseBodyArg(String body) {
        if (body == null) {
            return keyValue("responseBody", null);
        }
        String trimmedBody = body.trim();
        try {
            if (trimmedBody.startsWith("{") && trimmedBody.endsWith("}")) {
                objectMapper.readTree(trimmedBody);
                return raw("responseBody", trimmedBody);
            }
        } catch (JsonProcessingException e) {
            return keyValue("responseBody", body);
        }
        return keyValue("responseBody", body);
    }

    private boolean isSensitive(String name) {
        String lower = name.toLowerCase();
        return lower.contains("password") || lower.contains("token") || lower.contains("secret") 
            || lower.contains("credential") || lower.contains("key");
    }
    
    private void cleanupMdcKeys(String... keys) {
        for (String key : keys) {
            MDC.remove(key);
        }
    }
}

