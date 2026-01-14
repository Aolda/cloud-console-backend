package com.acc.global.handler;

import static net.logstash.logback.argument.StructuredArguments.raw;
import static net.logstash.logback.argument.StructuredArguments.value;

import com.acc.global.exception.AccBaseException;
import com.acc.global.exception.ErrorCode;
import com.acc.global.exception.ErrorResponse;
import com.acc.global.exception.common.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(AccBaseException.class)
    public ResponseEntity<ErrorResponse> handleAccBaseException(AccBaseException ex, HttpServletRequest request) {
        setupExceptionContext(ex, request);
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = HttpStatus.valueOf(errorCode.getStatus());
        Object stackTraceArg = value("stackTrace", getStackTraceString(ex, 10));

        if (status.is5xxServerError()) {
            log.error("[Exception] Server Error - Code: {}, Message: {}", errorCode.getCode(), ex.getCustomMessage(), stackTraceArg);
        } else {
            log.warn("[Exception] Client Error - Code: {}, Message: {}", errorCode.getCode(), ex.getCustomMessage(), stackTraceArg);
        }

        cleanupExceptionContext();
        return ResponseEntity.status(status).body(new ErrorResponse(errorCode, ex.getCustomMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        setupExceptionContext(ex, request);
        
        Object stackTraceArg = value("stackTrace", getStackTraceString(ex, 10));
        log.error("[Exception] Unhandled System Error - {}", ex.getMessage(), stackTraceArg);
        
        cleanupExceptionContext();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(CommonErrorCode.INTERNAL_FAILURE, ex.getMessage()));
    }


    private void setupExceptionContext(Exception ex, HttpServletRequest request) {
        MDC.put("type", "EXCEPTION");
        MDC.put("exceptionClass", ex.getClass().getSimpleName());
        MDC.put("path", request.getRequestURI());
        
        if (MDC.get("userId") == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                MDC.put("userId", auth.getName());
            } else {
                MDC.put("userId", "anonymous");
            }
        }
        
    }
    
    private void cleanupExceptionContext() {
        MDC.remove("exceptionClass");
        MDC.remove("stackTrace");
        MDC.remove("type");
        MDC.remove("path");
    }

    private String getStackTraceString(Throwable throwable, int maxLines) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        
        sb.append(throwable.toString());
        for (int i = 0; i < Math.min(stackTrace.length, maxLines); i++) {
            sb.append("\n\tat ").append(stackTrace[i].toString());
        }
        
        if (stackTrace.length > maxLines) {
            sb.append("\n\t... ").append(stackTrace.length - maxLines).append(" more");
        }
        
        // Caused by 추가 (있는 경우, 첫 번째 원인만)
        Throwable cause = throwable.getCause();
        if (cause != null) {
            sb.append("\nCaused by: ").append(cause.toString());
            StackTraceElement[] causeTrace = cause.getStackTrace();
            for (int i = 0; i < Math.min(causeTrace.length, 3); i++) {
                sb.append("\n\tat ").append(causeTrace[i].toString());
            }
            if (causeTrace.length > 3) {
                sb.append("\n\t... ").append(causeTrace.length - 3).append(" more");
            }
        }
        
        return sb.toString();
    }
}

