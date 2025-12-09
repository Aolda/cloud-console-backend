package com.acc.gitlab.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GitLabBotWebClientConfig {

    @Bean
    public WebClient gitLabBotWebClient() {
        return WebClient.builder().build();
    }
}

