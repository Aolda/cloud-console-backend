package com.acc.global.security.oauth.dto;

import com.acc.local.domain.enums.UnivAccountType;
import com.acc.local.dto.auth.UserDepartDto;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

public record OAuth2UserInfo(
    String id,
    String email,
    String username,
    String givenName,
    String familyName,
    String picture,
    String department,
    UnivAccountType univAccountType
) {

    public static OAuth2UserInfo from(OAuth2User oAuth2User) {
        return new OAuth2UserInfo(
                getAttributeAsString(oAuth2User, "id"),
                getAttributeAsString(oAuth2User, "email"),
                getAttributeAsString(oAuth2User, "name"),
                getAttributeAsString(oAuth2User, "givenName"),
                getAttributeAsString(oAuth2User, "familyName"),
                getAttributeAsString(oAuth2User, "picture"),
                getAttributeAsString(oAuth2User, "department"),
                null
        );
    }

    public static OAuth2UserInfo create(OAuth2User oAuth2User, UserDepartDto userDepartDto) {
        return new OAuth2UserInfo(
                getAttributeAsString(oAuth2User, "id"),
                getAttributeAsString(oAuth2User, "email"),
                getAttributeAsString(oAuth2User, "name"),
                getAttributeAsString(oAuth2User, "givenName"),
                getAttributeAsString(oAuth2User, "familyName"),
                getAttributeAsString(oAuth2User, "picture"),
                userDepartDto.department(),
                userDepartDto.univAccountType()
        );
    }

    private static String getAttributeAsString(OAuth2User oAuth2User, String key) {
        Object value = oAuth2User.getAttribute(key);
        return value != null ? value.toString() : null;
    }

    public Map<String, Object> toAttributeMap() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", id);
        attributes.put("email", email);
        attributes.put("name", username);
        attributes.put("givenName", givenName);
        attributes.put("familyName", familyName);
        attributes.put("picture", picture);
        attributes.put("department", department);
        return attributes;
    }

    public void validate() {
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("OAuth2 사용자 이메일 정보가 없습니다.");
        }
        if (id == null || id.isBlank()) {
            throw new OAuth2AuthenticationException("OAuth2 사용자 ID 정보가 없습니다.");
        }
    }
}
