package com.acc.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    /**
     * Spring Boot 기본 ObjectMapper를 명시적으로 선언.
     * 추후 별도 ObjectMapper 빈(redisObjectMapper 등)이 생성되면, auto-config이 생략되므로,
     * 기존 REST API 직렬화에 영향이 없도록 @Primary로 유지합니다.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.build();
    }
}
