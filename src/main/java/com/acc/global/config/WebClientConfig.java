package com.acc.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${openstack.url}")
    private String openstackUrl;

    @Bean
    public WebClient openstackWebClient() {
        return WebClient.builder()
                .baseUrl(openstackUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient keystoneWebClient() {
        return WebClient.builder()
                .baseUrl(openstackUrl + ":5000")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient glanceWebClient() {
        return WebClient.builder()
                .baseUrl(openstackUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient computeWebClient() {
        return WebClient.builder()
                .baseUrl(openstackUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient discordWebClient() {
        return WebClient.builder()
                .baseUrl("https://discord.com/api/webhooks")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

