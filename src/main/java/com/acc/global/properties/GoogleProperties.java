package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google")
@Getter
@Setter
public class GoogleProperties {
    private String serviceName;

    private String credentialsPath;
    private Spreadsheet spreadsheet = new Spreadsheet();

    @Getter @Setter
    public static class Spreadsheet {
        private String id;
        private String range;
    }
}

