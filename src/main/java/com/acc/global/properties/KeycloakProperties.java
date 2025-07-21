package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "keycloak") // yml 설정과 매핑
@Getter
@Setter
public class KeycloakProperties {
    private String baseUrl;
    private String realm;
    private String clientId;
    private String redirectUri;
    private String responseType = "code";
    private String scope = "openid";
    
    public String getLoginUrl() {
        return String.format("%s/realms/%s/protocol/openid-connect/auth?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s",
                baseUrl, realm, clientId, redirectUri, responseType, scope);
    }
}