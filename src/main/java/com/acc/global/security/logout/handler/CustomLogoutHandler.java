package com.acc.global.security.logout.handler;

import com.acc.local.service.modules.auth.AuthModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final AuthModule authModule;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            log.warn("[Auth] Logout - Authentication is null");
            return;
        }

        String userId = authentication.getName();
        MDC.put("type", "ACCESS");
        MDC.put("userId", userId);

        try {
             authModule.invalidateServiceTokensByUserId(userId);
            log.info("[Auth] Logout - Token Invalidation Success - User: {}", userId);
        } catch (Exception e) {
            log.error("[Auth] Logout - Token Invalidation Failed - User: {}", userId, e);
        } finally {
            MDC.remove("type");
            MDC.remove("userId");
        }
    }
}
