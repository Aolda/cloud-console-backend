package com.acc.global.security.oauth.handler;

import com.acc.global.exception.ErrorCode;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.OAuth2Exception;
import com.acc.global.properties.OAuth2Properties;
import com.acc.global.security.oauth.dto.OAuth2UserInfo;
import com.acc.local.domain.enums.UnivAccountType;
import com.acc.local.dto.auth.UserDepartDto;
import com.acc.local.service.modules.auth.AjouUnivModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final OAuth2Properties oAuth2Properties;
    private final AjouUnivModule ajouUnivModule;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Optional<UserDepartDto> userDepartDtoOpt = ajouUnivModule.getUserDepartInfo(oAuth2User);

        // 학적정보가 있는 경우, UNDERGRADUATE(재학생)인지 검증
        if (userDepartDtoOpt.isPresent()) {
            UserDepartDto userDepartDto = userDepartDtoOpt.get();

            // UNDERGRADUATE가 아닌 경우 에러 처리
            if (userDepartDto.univAccountType() != UnivAccountType.UNDERGRADUATE) {
                log.warn("OAuth2 인증 실패 - 재학생이 아님: {}, Type: {}",
                        oAuth2User.getAttribute("email"), userDepartDto.univAccountType());

                String errorCode = AuthErrorCode.ONLY_UNDERGRADUATE_ALLOWED.getCode();
                String redirectUrl = oAuth2Properties.getFailure().getRedirectUrl()
                        + "?error=" + URLEncoder.encode(errorCode, StandardCharsets.UTF_8);

                response.sendRedirect(redirectUrl);
                return;
            }
        }

        // 학적정보 유무와 관계없이 from 메서드 사용 (null 허용)
        OAuth2UserInfo userInfo = OAuth2UserInfo.from(oAuth2User);
        log.info("OAuth2 인증 성공 - Email: {}", userInfo.email());

        // 사용자 정보를 JSON으로 변환
        String userInfoJson = objectMapper.writeValueAsString(userInfo);

        // Base64 URL-Safe 인코딩 (UTF-8로 바이트 변환)
        String encodedUserInfo = Base64.getUrlEncoder()
                .encodeToString(userInfoJson.getBytes(StandardCharsets.UTF_8));

        // 쿠키 생성
        Cookie userInfoCookie = getUserInfoCookie(encodedUserInfo);

        response.addCookie(userInfoCookie);

        log.info("OAuth2 사용자 정보 쿠키 생성 완료 - 프론트엔드로 리다이렉트: {}", oAuth2Properties.getSuccess().getRedirectUrl());

        // 프론트엔드로 리다이렉트
        response.sendRedirect(oAuth2Properties.getSuccess().getRedirectUrl());
    }

    private Cookie getUserInfoCookie(String userInfoJson) {

        // 쿠키 생성
        Cookie userInfoCookie = new Cookie("oauth-user-info", userInfoJson);
        userInfoCookie.setHttpOnly(false); // 프론트엔드에서 JavaScript로 읽어야 하므로 false
        userInfoCookie.setSecure(true);   // 개발 환경에서는 false, 프로덕션에서는 true로 변경
        userInfoCookie.setPath("/");
        userInfoCookie.setMaxAge(900);     // 10분 (회원가입 완료 전까지만 유지)

        return userInfoCookie;
    }
}

