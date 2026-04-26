package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakProperties {
    private String issuerUri;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String frontendRedirectUrl;
    private String adminGroupPath;
}
