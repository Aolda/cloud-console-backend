package com.acc.global.security.oauth.handler;

import com.acc.global.exception.AccBaseException;
import com.acc.global.properties.OAuth2Properties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2Properties oAuth2Properties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        MDC.put("type", "AUTH_FAILURE");
        MDC.put("exceptionClass", exception.getClass().getSimpleName());
        
        String errorCode;

        // ACC 커스텀 예외 처리
        if (exception.getCause() instanceof AccBaseException accException) {
            errorCode = accException.getErrorCode().getCode();
            MDC.put("errorCode", errorCode);
            log.warn("[Auth] OAuth2 Failure (ACC) - Code: {}, Message: {}", 
                    errorCode, accException.getErrorCode().getMessage());
        }
        // 일반 OAuth2 예외 처리
        else {
            errorCode = "OAUTH2_AUTHENTICATION_FAILED";
            log.error("[Auth] OAuth2 Failure (General) - Message: {}", exception.getMessage(), exception);
        }

        String redirectUrl = oAuth2Properties.getFailure().getRedirectUrl() + "?error=" + URLEncoder.encode(errorCode, StandardCharsets.UTF_8);

        log.info("[Auth] Redirecting to Failure URL - {}", redirectUrl);
        
        MDC.remove("type");
        MDC.remove("exceptionClass");
        MDC.remove("errorCode");
        
        response.sendRedirect(redirectUrl);
    }
}
