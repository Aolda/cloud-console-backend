package com.acc.server.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "google.spreadsheet")
@Getter @Setter
public class GoogleSheetProperties {

    private String id;
    private String range;
}
