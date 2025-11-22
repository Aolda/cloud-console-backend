package com.acc.local.controller.docs;

import com.acc.local.dto.quickstart.QuickStartRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/quick-start")
@Tag(name = "Quick Start", description = "빠른 생성 관련 API")
@SecurityRequirement(name = "access-token")
public interface QuickStartDocs {

    @PostMapping
    @Operation(
            summary = "빠른 생성",
            description = "지정된 설정으로 인스턴스를 빠르게 생성합니다. 인스턴스 이름, flavor, 볼륨 크기, 비밀번호, 외부 네트워크 연결 여부를 설정할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인스턴스 생성 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 필수 파라미터 누락 또는 유효하지 않은 값",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 또는 flavor를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌 - 동일한 이름의 인스턴스가 이미 존재함",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 - 인스턴스 생성 실패",
                    content = @Content()
            )
    })
    ResponseEntity<Object> create(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "인스턴스 생성 요청 정보", required = true)
            @RequestBody QuickStartRequest request);
}
