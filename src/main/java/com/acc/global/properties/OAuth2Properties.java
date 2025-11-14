package com.acc.global.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth")
@Getter
@Setter
public class OAuth2Properties {
    private Success success;
    private Failure failure;

    @Getter
    @Setter
    public static class Success {
        private String redirectUrl;
    }

    @Getter
    @Setter
    public static class Failure {
        private String redirectUrl;
    }
}