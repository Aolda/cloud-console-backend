package com.acc.global.logging;

import com.acc.global.annotation.LogDomain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DomainLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LogDomain logDomain = handlerMethod.getBeanType().getAnnotation(LogDomain.class);
            if (logDomain != null) {
                MDC.put("domain", logDomain.value());
            } else {
                MDC.put("domain", "common");
            }
        }

        return true;
    }
}
