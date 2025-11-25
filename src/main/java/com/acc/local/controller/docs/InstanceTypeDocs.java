package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.type.InstanceTypeCreateRequest;
import com.acc.local.dto.type.InstanceTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "InstanceType", description = "인스턴스 타입(Flavor) API")
public interface InstanceTypeDocs {

    @Operation(
            summary = "[관리자] 인스턴스 타입 생성",
            description = "새로운 인스턴스 타입(Flavor)을 생성합니다. 아키텍처, 목적(Purpose), USB 지원 여부 등 확장 속성을 포함하여 OpenStack에 생성 요청을 수행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "인스턴스 타입 생성 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 필수 파라미터 누락",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한 필요",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack 호출 실패",
                    content = @Content()
            )
    })
    @PostMapping("/api/v1/admin/types")
    ResponseEntity<Object> createInstanceType(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody
            @Parameter(description = "인스턴스 타입 생성 요청 정보", required = true)
            InstanceTypeCreateRequest request);


    @Operation(
            summary = "[관리자] 인스턴스 타입 목록 조회",
            description = "관리자가 아키텍처 필터링으로 인스턴스 타입 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한 필요",
                    content = @Content()
            )
    })
    @GetMapping("/api/v1/admin/types")
    ResponseEntity<PageResponse<InstanceTypeResponse>> getAdminInstanceTypes(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "페이지 정보 (Marker 기반)", required = false)
            PageRequest page,
            @Parameter(description = "아키텍처 필터 (예: X86)", required = false)
            @RequestParam(required = false) String architect);


    @Operation(
            summary = "[사용자] 인스턴스 타입 목록 조회",
            description = "사용자가 생성 가능한 인스턴스 타입 목록을 조회합니다. 권한이 있는 Private 타입과 Public 타입이 반환됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            )
    })
    @GetMapping("/api/v1/types")
    ResponseEntity<PageResponse<InstanceTypeResponse>> getUserInstanceTypes(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "페이지 정보 (Marker 기반)", required = false)
            PageRequest page,
            @Parameter(description = "아키텍처 필터 (예: X86)", required = false)
            @RequestParam(required = false) String architect);
}
