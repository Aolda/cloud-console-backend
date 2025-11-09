package com.acc.global.security.oauth.provider;

import com.acc.global.security.oauth.dto.OAuth2UserInfo;

import java.util.Map;

public class GoogleOAuth2Provider implements OAuth2Provider {

    @Override
    public OAuth2UserInfo extractUserInfo(Map<String, Object> attributes) {
        return new OAuth2UserInfo(
                getAttribute(attributes, "id"),
                getAttribute(attributes, "email"),
                getAttribute(attributes, "name"),
                getAttribute(attributes, "given_name"),
                getAttribute(attributes, "family_name"),
                getAttribute(attributes, "picture"),
                null,
                null
        );
    }

    private String getAttribute(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : null;
    }
}
