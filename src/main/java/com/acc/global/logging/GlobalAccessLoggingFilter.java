package com.acc.global.logging;

import com.acc.global.logging.enums.Sensitive;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import static net.logstash.logback.argument.StructuredArguments.raw;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalAccessLoggingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.matches(".+\\.(css|js|ico|png|jpg|gif)$");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logRequest(request, response, duration);
        }
    }

    private void logRequest(HttpServletRequest request, HttpServletResponse response, long duration) {
        try {
            int status = response.getStatus();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            
            MDC.put("type", "ACCESS");
            MDC.put("method", method);
            MDC.put("uri", uri);
            MDC.put("status", String.valueOf(status));
            MDC.put("durationMs", String.valueOf(duration));
            MDC.put("clientIp", getClientIp(request));
            MDC.put("userId", getUserId());
            
            if (request.getQueryString() != null) MDC.put("queryParams", decoding(request.getQueryString()));
            if (status >= 400) {
                String body = getRequestBody(request);
                String sanitizedBody = null;
                if (body != null && !body.isEmpty()) {
                    sanitizedBody = sanitizeBody(body);
                }

                Object bodyArg = (sanitizedBody != null) ? raw("requestBody", sanitizedBody) : null;

                if (status >= 500) {
                    if (bodyArg != null) {
                        log.error("[Access] Server Error - {} {}", method, uri, bodyArg);
                    } else {
                        log.error("[Access] Server Error - {} {}", method, uri);
                    }
                } else {
                    if (bodyArg != null) {
                        log.warn("[Access] Client Error - {} {}", method, uri, bodyArg);
                    } else {
                        log.warn("[Access] Client Error - {} {}", method, uri);
                    }
                }
            } else {
                log.info("[Access] Success - {} {}", method, uri);
            }
        } catch (Exception e) {
            log.error("[Access] Logging Failed", e);
        } finally {
            MDC.clear();
        }
    }

    private String getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "anonymous";
    }

    private String getRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 1024 * 1024) {
                return "[Large Payload: " + buf.length + " bytes]";
            }

            if (buf.length > 0) {
                try { return new String(buf, 0, buf.length, wrapper.getCharacterEncoding()); } 
                catch (Exception e) { return "[Binary/Unparseable]"; }
            }
        }
        return null; 
    }

    private String sanitizeBody(String body) {
        return body.replaceAll("(?i)\"" + Sensitive.SENSITIVES_REGEX + "\"\\s*:\\s*\"[^\"]+\"", "\"$1\":\"***\"");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip == null || ip.isEmpty()) ? request.getRemoteAddr() : ip;
    }

    private String decoding(String value) {
        try {
            return java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}

