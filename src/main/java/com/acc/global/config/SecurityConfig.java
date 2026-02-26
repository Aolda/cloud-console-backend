package com.acc.global.config;

import com.acc.global.logging.GlobalAccessLoggingFilter;
import com.acc.global.logging.RequestCachingFilter;
import com.acc.global.security.oauth.OAuth2CustomUserService;
import com.acc.global.security.oauth.handler.OAuthFailureHandler;
import com.acc.global.security.oauth.handler.OAuthSuccessHandler;
import com.acc.global.security.session.SessionAuthenticationFilter;
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

    private final SessionAuthenticationFilter sessionAuthenticationFilter;
    private final RequestCachingFilter requestCachingFilter;
    private final GlobalAccessLoggingFilter globalAccessLoggingFilter;
    //OAuth
    private final OAuth2CustomUserService oAuth2CustomUserService;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final OAuthFailureHandler oAuthFailureHandler;

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
                                "/test",
                                "/api/v1/google/**",
                                "/api/v1/auth/token",
                                "/api/v1/auth/login/general",
                                "/api/v1/auth/login",
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login/refresh",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/api/v1/images/**",
                                "/api/v1/projects/*/images",
                                "/api/v1/snapshots/**",
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs",
                                "/actuator/**",
                                "/api/v1/auth/keycloak/login",
                                "/api/v1/auth/keycloak/callback"

                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // Filter Order: RequestCaching → SessionAuth → AccessLogging
                //
                // SessionAuthenticationFilter: acc-session-id 쿠키 있으면 SessionPrincipal 로 인증
                .addFilterBefore(requestCachingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(sessionAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(globalAccessLoggingFilter, SessionAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "https://script.google.com",
                "https://script.googleusercontent.com",
                "https://console.aoldacloud.com",
                "https://acc.jalju.com",
                "https://dev.aoldacloud.com",
                "https://dev.aoldacloud.com:5173",
                "https://console.jalju.com",
                "https://console.jalju.com:5173",
                "https://aolda.cloud",
                "https://console.aolda.cloud"
        ));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
