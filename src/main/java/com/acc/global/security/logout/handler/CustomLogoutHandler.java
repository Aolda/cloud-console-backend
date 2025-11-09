package com.acc.global.security.logout.handler;

import com.acc.local.service.modules.auth.AuthModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final AuthModule authModule;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            log.warn("logout - authentication is null.");
            return;
        }

        String userId = authentication.getName();

        try {
             authModule.invalidateServiceTokensByUserId(userId);
            log.info("logout - 유저 토큰 삭제 성공 user: {}", userId);
        } catch (Exception e) {
            log.error("logout - 유저 토큰 삭제 실패 user: {}", userId, e);
        }
    }
}
