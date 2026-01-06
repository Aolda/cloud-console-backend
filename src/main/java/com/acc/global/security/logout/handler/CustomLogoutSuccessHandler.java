package com.acc.global.security.logout.handler;

import com.acc.global.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import org.slf4j.MDC;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String userId = "anonymous";
        if (authentication != null) {
            userId = authentication.getName();
        }

        MDC.put("type", "ACCESS");
        MDC.put("userId", userId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());
        
        log.info("[Auth] Logout Success - User: {}", userId);

        MDC.clear();

        ApiResponse<Void> apiResponse = ApiResponse.success("로그아웃되었습니다.");

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
