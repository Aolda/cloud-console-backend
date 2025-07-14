package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//개발용
@Component
@ConfigurationProperties(prefix = "openstack")
@Getter
@Setter
public class OpenStackProperties {
    private String url;
    private Keystone keystone = new Keystone();

    @Getter
    @Setter
    public static class Keystone {
        private String username;
        private String password;
        private String project;
    }
} 
