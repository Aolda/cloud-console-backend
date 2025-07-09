package com.acc.server.global.properties.external;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cinder")
@Getter @Setter
public class CinderProperties {
    private String baseUrl;
    private String volumesUrl;
    private String volumesDetailUrl;
}


//base-url: ${CINDER_BASE_URL}
//volumes-url: ${CINDER_VOLUMES_URL}
//volumes-detail-url: ${CINDER_VOLUMES_DETAIL_URL}
