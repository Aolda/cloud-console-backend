package com.acc.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/google/**",
                                "/api/v1/auth/token",
                                "/api/v1/computes/**",
                                "/api/v1/images/**",
                                "/api/v1/projects",
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
