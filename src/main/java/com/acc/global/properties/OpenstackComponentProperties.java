package com.acc.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "acc.openstack")
@Getter
@Setter
public class OpenstackComponentProperties {

    private Map<String, Component> components = new HashMap<>();
    private Resilience resilience = new Resilience();

    @Getter
    @Setter
    public static class Component {
        private int port;
    }

    @Getter
    @Setter
    public static class Resilience {
        private String defaultComponent = "default";
    }
}
