package com.acc.server.local.service.modules.keystoneToken;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "keystone")
public class KeystoneProperties {
    private String baseUrl;
    private String tokenUrl;
}
