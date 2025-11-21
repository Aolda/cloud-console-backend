package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/admin/users")
@Tag(name = "Admin Users", description = "관리자 사용자 관리 API")
public interface AdminUserDocs {

    @Operation(
            summary = "사용자 생성",
            description = "관리자가 새로운 사용자를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 생성 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 학번은 7개의 문자로 이뤄져야합니다.",
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
    @PostMapping("")
    ResponseEntity<AdminCreateUserResponse> createUser(
            @RequestBody
            @Parameter(description = "사용자 생성 요청 정보", required = true)
            AdminCreateUserRequest request,
            Authentication authentication);


    @Operation(
            summary = "사용자 수정",
            description = "관리자가 기존 사용자 정보를 수정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 수정 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 학번은 7개의 문자로 이뤄져야합니다.",
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
                    responseCode = "404",
                    description = "사용자 없음 - 수정할 사용자 객체를 찾을 수 없습니다",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack Keystone API 통신 중 오류가 발생했습니다.",
                    content = @Content()
            )
    })
    @PutMapping("")
    ResponseEntity<AdminUpdateUserResponse> updateUser(
            @RequestBody
            @Parameter(description = "사용자 수정 요청 정보", required = true)
            AdminUpdateUserRequest request,
            Authentication authentication,
            @RequestParam @Parameter(description = "사용자 키스톤 유저 아이디", required = true) String userId);


    @Operation(
            summary = "사용자 상세 조회",
            description = "관리자가 특정 사용자의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 조회 성공",
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
                    responseCode = "404",
                    description = "사용자 없음 - 사용자를 찾을 수 없습니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - Keystone 통신 중 에러가 발생했습니다.",
                    content = @Content()
            )
    })
    @GetMapping(params = "userId")
    ResponseEntity<AdminGetUserResponse> getUser(
            @RequestParam
            @Parameter(description = "조회할 사용자 키스톤 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            String userId,
            Authentication authentication);


    @Operation(
            summary = "사용자 목록 조회",
            description = "관리자가 전체 사용자 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 목록 조회 성공",
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
    @GetMapping
    ResponseEntity<PageResponse<AdminListUsersResponse>> listUsers(
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page,
            Authentication authentication);


    @Operation(
            summary = "사용자 삭제",
            description = "관리자가 사용자를 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "사용자 삭제 성공",
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
                    responseCode = "404",
                    description = "사용자 없음 - 삭제할 사용자를 찾을 수 없습니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - Keystone 통신 중 에러가 발생했습니다.",
                    content = @Content()
            )
    })
    @DeleteMapping("")
    ResponseEntity<Void> deleteUser(
            @RequestParam
            @Parameter(description = "삭제할 사용자 ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            String userId,
            Authentication authentication);
}
