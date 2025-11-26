package com.acc.local.controller.docs;

import com.acc.local.dto.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증/인가 관련 API")
@SecurityRequirement(name = "access-token")
public interface AuthDocs {

    // ------------------------- LOGIN -------------------------
    @Operation(
            summary = "로그인",
            description = "사용자가 Keystone 아이디/패스워드로 로그인합니다.<br>" +
                    "성공 시 AccessToken은 Body로, RefreshToken은 Cookie에 저장됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content()),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(
            @RequestBody
            @Parameter(description = "Keystone 로그인 정보", required = true)
            KeystonePasswordLoginRequest request,
            HttpServletResponse response
    );


    // ------------------------- PROJECT TOKEN -------------------------
    @Operation(
            summary = "프로젝트 스코프 토큰 발급",
            description = "Access Token(JWT)에서 userId를 추출하여 Keystone 프로젝트 스코프 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발급 성공", content = @Content()),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    @PostMapping("/tokens/project")
    ResponseEntity<ProjectTokenResponse> issueProjectToken(
            @RequestBody
            @Parameter(description = "프로젝트 ID", required = true)
            ProjectTokenRequest request,
            @Parameter(hidden = true) Authentication authentication
    );


    // ------------------------- REFRESH TOKEN -------------------------
    @Operation(
            summary = "Access Token 재발급",
            description = "Refresh Token Cookie를 기반으로 새로운 Access Token을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content()),
            @ApiResponse(responseCode = "401", description = "Refresh Token 만료/무효", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    @PostMapping("/login/refresh")
    ResponseEntity<LoginResponse> refreshToken(
            @CookieValue("acc-refresh-token")
            @Parameter(description = "Refresh Token Cookie", required = true)
            String refreshToken
    );


    // ------------------------- SIGNUP -------------------------
    @Operation(
            summary = "회원가입",
            description = "OAuth를 통해 pre-authenticated 된 사용자 정보를 기반으로 회원가입합니다.<br>" +
                    "OAuth 로그인 성공 시 발급된 검증 토큰(Cookie)이 필요합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content()),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 검증 토큰이 없거나 유효하지 않음", content = @Content()),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 검증 토큰 만료 또는 이미 사용됨", content = @Content()),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자", content = @Content()),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    @PostMapping("/signup")
    ResponseEntity<SignupResponse> signup(
            @RequestBody
            @Parameter(description = "회원가입 요청 정보", required = true)
            SignupRequest request,
            @CookieValue("oauth-verification-token")
            @Parameter(description = "OAuth 검증 토큰 Cookie (OAuth 로그인 시 자동 발급)", required = true)
            String verificationToken
    );

    // ------------------------- SIGNUP -------------------------
    @Operation(
        summary = "회원정보 조회 - 기본",
        description = "로그인된 사용자의 기본정보를 조회합니다"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조호 성공", content = @Content()),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - 검증 토큰이 없거나 유효하지 않음", content = @Content()),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 검증 토큰 만료 또는 이미 사용됨", content = @Content()),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    @PostMapping("/profile")
    ResponseEntity<LoginedUserProfileResponse> getLoginUserInformation(
        @Parameter(hidden = true)
        Authentication authentication
    );
}

