package com.acc.server.global.properties.external;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "keystone")
@Getter @Setter
public class KeystoneProperties {
    private String baseUrl;
    private String tokenUrl;
}
