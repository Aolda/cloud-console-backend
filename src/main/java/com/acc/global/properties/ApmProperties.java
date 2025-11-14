package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "apm")
@Getter
@Setter
public class ApmProperties {
    private String serverUrl;
}
