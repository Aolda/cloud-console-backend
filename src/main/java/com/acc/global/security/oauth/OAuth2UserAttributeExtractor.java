package com.acc.global.security.oauth;

import com.acc.global.security.oauth.provider.GoogleOAuth2Provider;
import com.acc.global.security.oauth.provider.OAuth2Provider;
import com.acc.global.security.oauth.dto.OAuth2UserInfo;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 추후, 깃랩 계정 추가를 위함
 */
@Component
public class OAuth2UserAttributeExtractor {

    private static final Map<String, OAuth2Provider> PROVIDERS = Map.of(
            "google", new GoogleOAuth2Provider()
    );

    public OAuth2UserInfo extract(String registrationId, Map<String, Object> attributes) {
        OAuth2Provider provider = PROVIDERS.get(registrationId.toLowerCase());

        if (provider == null) {
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth 공급자: " + registrationId);
        }

        OAuth2UserInfo userInfo = provider.extractUserInfo(attributes);
        userInfo.validate();
        return userInfo;
    }
}
