package com.acc.server.local.service.modules.cinder;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cinder")
public class CinderApiProperties {
    private String baseUrl;
    private String volumesUrl;
    private String volumesDetailUrl;
}
