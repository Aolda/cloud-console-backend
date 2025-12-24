package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "quick-start")
@Getter
@Setter
public class QuickStartProperties {
    private String defaultImageId;
}
