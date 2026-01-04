package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.ErrorResponse;
import com.acc.local.dto.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;

@RequestMapping("/api/v1/admin/notices")
@Tag(name = "Admin Notices", description = "관리자 공지 관리 API")
@SecurityRequirement(name = "access-token")
public interface NoticeDocs {

    @Operation(
            summary = "공지 생성(업서트)",
            description = "관리자가 공지를 생성합니다.\n\n"
                    + "- 본 시스템의 공지는 단일 개념으로 관리됩니다.\n"
                    + "- 동일 개념의 공지가 존재하면 기존 공지를 갱신(업서트)합니다.\n"
                    + "- 날짜 필드(startsAt/endsAt)는 ISO-8601 형식(예: 2025-01-01T09:00:00)을 사용하세요."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "공지 생성 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 필수 파라미터가 누락되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한이 필요한 기능입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - ACC 서버 에러가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("")
    ResponseEntity<CreateNoticeResponse> createNotice(
            @RequestBody(required = true)
            @Parameter(description = "공지 생성 요청 정보", required = true)
            CreateNoticeRequest request,
            @Parameter(hidden = true) Authentication authentication);

    @Operation(
            summary = "공지 목록 조회",
            description = "관리자가 전체 공지 목록을 조회합니다.\n\n"
                    + "- 페이지네이션(마커 기반)\n"
                    + "  - marker: 경계 ID(첫 조회 null 또는 빈 문자열)\n"
                    + "  - direction: next(기본) | prev\n"
                    + "  - limit: 기본 10, 전체는 limit=0\n"
                    + "  - next: id > marker 오름차순 / prev: id < marker 내려받아 뒤집어 반환(클라이언트는 오름차순 유지)\n"
                    + "- 예시: GET /api/v1/admin/notices?marker=abc123&direction=next&limit=10"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "공지 목록 조회 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 잘못된 파라미터 형식입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한이 필요한 기능입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - Acc 서버 내부 에러가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("")
    ResponseEntity<PageResponse<ListNoticesResponse>> listNotice(
            @Parameter(description = "페이지 정보", required = false)
            @ParameterObject PageRequest page,
            @Parameter(hidden = true) Authentication authentication);

    // 공지 생성 API는 단일 공지 개념으로 동작하며,
    // 동일 개념의 공지를 재요청 시 기존 공지를 갱신(업서트)하는 것으로 사용합니다.
}
