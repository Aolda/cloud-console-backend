package com.acc.global.security.oauth.provider;

import com.acc.global.security.oauth.dto.OAuth2UserInfo;

import java.util.Map;

public interface OAuth2Provider {
    OAuth2UserInfo extractUserInfo(Map<String, Object> attributes);
}
