package com.acc.local.controller.docs;

import com.acc.local.dto.auth.LoginedUserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증/인가 관련 API")
public interface AuthDocs {

    @Operation(
        summary = "회원정보 조회 - 기본",
        description = "로그인된 사용자의 기본정보를 조회합니다"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content()),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - 검증 토큰이 없거나 유효하지 않음", content = @Content()),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 검증 토큰 만료 또는 이미 사용됨", content = @Content()),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content())
    })
    @GetMapping("/profile")
    ResponseEntity<LoginedUserProfileResponse> getLoginUserInformation(
        @Parameter(hidden = true)
        Authentication authentication,
        @RequestParam(required = false)
        @Parameter(description = "프로젝트 ID", required = false)
        String projectId
    );
}
