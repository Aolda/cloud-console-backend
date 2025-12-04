package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/keypairs")
@Tag(name = "Keypair", description = "키페어 API")
@SecurityRequirement(name = "access-token")
public interface KeypairDocs {

    @Operation(
            summary = "키페어 목록 조회",
            description = "프로젝트에 속한 키페어 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "키페어 목록 조회 성공"
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
                    responseCode = "500",
                    description = "서버 오류 - DB 조회 오류",
                    content = @Content()
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<KeypairListResponse>> getKeypairs(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page);


    @Operation(
            summary = "키페어 생성",
            description = "새로운 키페어를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "키페어 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 유효하지 않은 키페어 이름 또는 OpenStack 요청 오류",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - OpenStack 인증 실패",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - OpenStack API 권한 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "프로젝트 없음 - DB에서 프로젝트를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이름 중복 - 이미 존재하는 키페어 이름",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack 생성 실패 또는 DB 저장 실패",
                    content = @Content()
            )
    })
    @PostMapping
    ResponseEntity<CreateKeypairResponse> createKeypair(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestBody
            @Parameter(description = "키페어 생성 요청 정보", required = true)
            CreateKeypairRequest request);


    @Operation(
            summary = "키페어 삭제",
            description = "지정한 키페어를 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "키페어 삭제 성공",
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
                    description = "키페어 없음 - DB에서 지정한 키페어를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack 삭제 실패 또는 DB 오류",
                    content = @Content()
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteKeypair(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "삭제할 키페어의 ID (핑거프린트)", required = true)
            String keypairId);
}
