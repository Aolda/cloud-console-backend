package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.CreateRoleRequest;
import com.acc.local.dto.auth.CreateRoleResponse;
import com.acc.local.dto.auth.ListRolesResponse;
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

@RequestMapping("/api/v1/admin/roles")
@Tag(name = "Admin Roles", description = "관리자 권한 관리 API")
@SecurityRequirement(name = "access-token")
public interface RoleDocs {

    @Operation(
            summary = "역할 생성",
            description = "관리자가 새로운 역할을 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "역할 생성 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 필수 파라미터가 누락되었습니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한이 필요한 기능입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌 - 이미 존재하는 역할입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - Keystone 통신 중 에러가 발생했습니다.",
                    content = @Content()
            )
    })
    @PostMapping("")
    ResponseEntity<CreateRoleResponse> createRole(
            @RequestBody
            @Parameter(description = "역할 생성 요청 정보", required = true)
            CreateRoleRequest request,
            @Parameter(hidden = true) Authentication authentication);

    @Operation(
            summary = "역할 목록 조회",
            description = "관리자가 전체 역할 목록을 조회합니다. name 파라미터로 역할 이름 검색이 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "역할 목록 조회 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 잘못된 파라미터 형식입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한이 필요한 기능입니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - Keystone 통신 중 에러가 발생했습니다.",
                    content = @Content()
            )
    })
    @GetMapping("")
    ResponseEntity<PageResponse<ListRolesResponse>> listRoles(
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page,
            @Parameter(description = "역할 이름으로 검색 (선택)", example = "admin")
            String name,
            @Parameter(hidden = true) Authentication authentication);
}
