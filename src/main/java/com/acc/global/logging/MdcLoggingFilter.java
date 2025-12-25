package com.acc.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class MdcLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = request.getHeader("X-Trace-ID");
            if (traceId == null || traceId.isEmpty()) {
                traceId = UUID.randomUUID().toString().substring(0, 8);
            }

            MDC.put("traceId", traceId);
            MDC.put("method", request.getMethod());
            MDC.put("uri", request.getRequestURI());
            filterChain.doFilter(request, response);
        } finally {
            log.info("Completed request: {} {}", request.getMethod(), request.getRequestURI());
            MDC.clear();
        }
    }
}
