package com.acc.server.openstack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${openstack.keystone.url}")
    private String keystoneUrl;

    @Value("${openstack.neutron.url}")
    private String neutronUrl;

    @Bean
    public WebClient keystoneWebClient() {
        return WebClient.builder()
                .baseUrl(keystoneUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient neutronWebClient() {
        return WebClient.builder()
                .baseUrl(neutronUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
