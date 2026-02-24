package com.acc.local.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/v1/auth/keycloak")
@Tag(name = "Keycloak Auth", description = "Keycloak OIDC 인증 관련 API")
public interface KeycloakAuthDocs {

    @Operation(
            summary = "Keycloak 로그인",
            description = "Keycloak 로그인 페이지로 리다이렉트합니다.<br>" +
                    "CSRF 방지를 위한 state 값을 쿠키에 설정한 후 Keycloak Authorization Endpoint로 302 리다이렉트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Keycloak 로그인 페이지로 리다이렉트", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    @GetMapping("/login")
    void login(HttpServletResponse response);

    @Operation(
            summary = "Keycloak OIDC 콜백",
            description = "Keycloak 로그인 후 콜백을 수신합니다.<br>" +
                    "인가코드를 토큰으로 교환하고, 세션을 생성한 후 프론트엔드로 리다이렉트합니다.<br>" +
                    "CSRF state 검증 → 토큰 교환 → 세션 생성 → 세션 쿠키 설정 → 프론트엔드 리다이렉트"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "프론트엔드로 리다이렉트 (세션 쿠키 설정됨)", content = @Content()),
            @ApiResponse(responseCode = "400", description = "CSRF state 불일치", content = @Content()),
            @ApiResponse(responseCode = "500", description = "토큰 교환 실패 또는 서버 오류", content = @Content())
    })
    @GetMapping("/callback")
    void callback(
            @RequestParam("code")
            @Parameter(description = "Keycloak 인가코드", required = true)
            String code,

            @RequestParam("state")
            @Parameter(description = "CSRF 방지용 state 파라미터", required = true)
            String state,

            HttpServletRequest request,
            HttpServletResponse response
    );

    @Operation(
            summary = "Keycloak 로그아웃",
            description = "현재 세션을 종료합니다.<br>" +
                    "Keycloak refresh_token 폐기 → Redis 세션 삭제 → acc-session-id 쿠키 삭제"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content()),
            @ApiResponse(responseCode = "401", description = "세션 없음 또는 만료", content = @Content())
    })
    @DeleteMapping("/logout")
    void logout(HttpServletRequest request, HttpServletResponse response);
}
