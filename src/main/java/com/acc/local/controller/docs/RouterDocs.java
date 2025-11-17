package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateRouterRequest;
import com.acc.local.dto.network.ViewRoutersResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/routers")
@Tag(name = "Router", description = "라우터 API")
public interface RouterDocs {

    @Operation(
            summary = "라우터 목록 조회",
            description = "프로젝트에 속한 라우터 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "라우터 목록 조회 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
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
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content()
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<ViewRoutersResponse>> viewRouters(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page);

    @Operation(
            summary = "라우터 생성",
            description = "새로운 라우터를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "라우터 생성 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
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
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content()
            )
    })
    @PostMapping
    ResponseEntity<Object> createRouter(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody
            @Parameter(description = "라우터 생성 요청 정보", required = true)
            CreateRouterRequest request);

    @Operation(
            summary = "라우터 삭제",
            description = "지정한 라우터를 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "라우터 삭제 성공",
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
                    description = "라우터 없음 - 지정한 라우터를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content()
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteNetwork(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam String routerId);
}
