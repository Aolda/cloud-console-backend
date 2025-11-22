package com.acc.global.config;

import com.acc.global.security.jwt.JwtAuthenticationFilter;
import com.acc.global.security.logout.handler.CustomLogoutHandler;
import com.acc.global.security.logout.handler.CustomLogoutSuccessHandler;
import com.acc.global.security.oauth.OAuth2CustomUserService;
import com.acc.global.security.oauth.handler.OAuthFailureHandler;
import com.acc.global.security.oauth.handler.OAuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    //OAuth
    private final OAuth2CustomUserService oAuth2CustomUserService;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final OAuthFailureHandler oAuthFailureHandler;

    //logout
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인증 실패(401)나 권한 부족(403) 시 로그인 페이지로 리다이렉트(302)하는 것을 방지
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(oAuth2CustomUserService))
                        .successHandler(oAuthSuccessHandler)
                        .failureHandler(oAuthFailureHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/google/**",
                                "/api/v1/auth/token",
                                "/api/v1/auth/login/general",
                                "/api/v1/auth/login",
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login/refresh",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/api/v1/images/**",
                                "/api/v1/projects",
                                "/api/v1/projects/*/images",
                                "/api/v1/snapshots/**",
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs"

                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(
                    logout -> logout
                            .logoutUrl("/api/v1/auth/logout")
                            .addLogoutHandler(customLogoutHandler)
                            .logoutSuccessHandler(customLogoutSuccessHandler)
                            .deleteCookies("acc-access-token","acc-refresh-token")
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "https://script.google.com",
                "https://script.googleusercontent.com",
                "https://console.aoldacloud.com",
                "https://acc.jalju.com"
        ));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/google/**", configuration);
        return source;
    }
}
