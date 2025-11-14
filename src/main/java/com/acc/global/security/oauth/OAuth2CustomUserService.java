package com.acc.global.security.oauth;

import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.OAuth2Exception;
import com.acc.global.security.oauth.dto.OAuth2UserInfo;
import com.acc.local.service.modules.auth.AuthModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2CustomUserService extends DefaultOAuth2UserService {

    private final AuthModule authModule;
    private final OAuth2UserAttributeExtractor attributeExtractor;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo userInfo = attributeExtractor.extract(registrationId, oAuth2User.getAttributes());

        validateUserNotExists(userInfo.email());

        return createOAuth2User(userInfo);
    }

    private void validateUserNotExists(String email) {
        if (authModule.isUserExistsByEmail(email)) {
            throw new OAuth2Exception(AuthErrorCode.OAUTH2_USER_ALREADY_EXISTS);
        }
    }

    private OAuth2User createOAuth2User(OAuth2UserInfo userInfo) {
        Map<String, Object> attributes = userInfo.toAttributeMap();
        log.info("OAuth2 인증 성공 - Email: {}", userInfo.email());

        return new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(attributes)),
                attributes,
                "email"
        );
    }
}