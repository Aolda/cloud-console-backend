package com.acc.global.security.oauth.handler;

import com.acc.global.exception.ErrorCode;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.OAuth2Exception;
import com.acc.global.properties.OAuth2Properties;
import com.acc.global.security.oauth.dto.OAuth2UserInfo;
import com.acc.local.domain.enums.UnivAccountType;
import com.acc.local.dto.auth.UserDepartDto;
import com.acc.local.service.modules.auth.AjouUnivModule;
import com.acc.local.service.modules.auth.AuthModule;
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
    private final AuthModule authModule;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Optional<UserDepartDto> userDepartDtoOpt = ajouUnivModule.getUserDepartInfo(oAuth2User);

        // 학과 정보 추출 로그
        log.info("[학과 정보 추출] Email: {}, 학과 정보 존재 여부: {}",
                oAuth2User.getAttribute("email"), userDepartDtoOpt.isPresent());

        // 학적 정보가 없는 경우 에러 처리
        if (userDepartDtoOpt.isEmpty()) {
            log.warn("OAuth2 인증 실패 - 학적 정보 없음: {}, OAuth2User attributes: {}",
                    oAuth2User.getAttribute("email"), oAuth2User.getAttributes());

            String errorCode = AuthErrorCode.NO_UNIV_ACCOUNT_INFO.getCode();
            String redirectUrl = oAuth2Properties.getFailure().getRedirectUrl()
                    + "?error=" + URLEncoder.encode(errorCode, StandardCharsets.UTF_8);

            response.sendRedirect(redirectUrl);
            return;
        }

        UserDepartDto userDepartDto = userDepartDtoOpt.get();
        log.info("[학과 정보 상세] 학과: {}, 학적 타입: {}",
                userDepartDto.department(), userDepartDto.univAccountType());

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

        // 재학생인 경우에만 정상 처리
        OAuth2UserInfo userInfo = OAuth2UserInfo.create(oAuth2User, userDepartDto);
        log.info("OAuth2 인증 성공 - Email: {}", userInfo.email());

        // 1. OAuth 검증 토큰 생성 및 DB 저장
        String verificationToken = authModule.generateOAuthVerificationToken(userInfo.email());
        log.info("OAuth 검증 토큰 생성 완료 - Email: {}", userInfo.email());

        // 2. 사용자 정보를 JSON으로 변환
        String userInfoJson = objectMapper.writeValueAsString(userInfo);

        // 3. Base64 URL-Safe 인코딩 (UTF-8로 바이트 변환)
        String encodedUserInfo = Base64.getUrlEncoder()
                .encodeToString(userInfoJson.getBytes(StandardCharsets.UTF_8));

        // 4. 사용자 정보 쿠키 생성 및 추가
        Cookie userInfoCookie = getUserInfoCookie(encodedUserInfo);
        response.addCookie(userInfoCookie);

        // 5. 검증 토큰 쿠키 생성 및 추가
        Cookie verificationTokenCookie = getVerificationTokenCookie(verificationToken);
        response.addCookie(verificationTokenCookie);

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
        userInfoCookie.setMaxAge(900);     // 15분 (회원가입 완료 전까지만 유지)

        // SameSite 속성 설정 (크로스 사이트 쿠키 허용)
        userInfoCookie.setAttribute("SameSite", "None");

        // 도메인 설정 (설정되어 있을 때만)
        String domain = oAuth2Properties.getCookie().getDomain();
        if (domain != null && !domain.isBlank()) {
            userInfoCookie.setDomain(domain);
            log.info("[쿠키 설정] oauth-user-info 쿠키 도메인: {}, SameSite: None", domain);
        } else {
            log.info("[쿠키 설정] oauth-user-info 쿠키 도메인: 미설정 (현재 호스트 사용), SameSite: None");
        }

        return userInfoCookie;
    }

    private Cookie getVerificationTokenCookie(String verificationToken) {
        // 검증 토큰 쿠키 생성
        Cookie verificationTokenCookie = new Cookie("oauth-verification-token", verificationToken);
        verificationTokenCookie.setHttpOnly(true); // JavaScript에서 접근 불가 (보안)
        verificationTokenCookie.setSecure(true);
        verificationTokenCookie.setPath("/");
        verificationTokenCookie.setMaxAge(900); // 15분 (검증 토큰과 동일한 만료시간)

        // SameSite 속성 설정 (크로스 사이트 쿠키 허용)
        verificationTokenCookie.setAttribute("SameSite", "None");

        // 도메인 설정 (설정되어 있을 때만)
        String domain = oAuth2Properties.getCookie().getDomain();
        if (domain != null && !domain.isBlank()) {
            verificationTokenCookie.setDomain(domain);
            log.info("[쿠키 설정] oauth-verification-token 쿠키 도메인: {}, SameSite: None", domain);
        } else {
            log.info("[쿠키 설정] oauth-verification-token 쿠키 도메인: 미설정 (현재 호스트 사용), SameSite: None");
        }

        return verificationTokenCookie;
    }
}

