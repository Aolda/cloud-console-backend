package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateSecurityRuleRequest;
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

@RequestMapping("/api/v1/security-rules")
@Tag(name = "Security Rule", description = "보안 규칙 API")
@SecurityRequirement(name = "access-token")
public interface SecurityRuleDocs {

    @Operation(
            summary = "보안 규칙 생성",
            description = "새로운 보안 규칙을 생성합니다. <br>" +
                    "remoteSecurityGroupId와 cidr 둘 중 하나만 제공할 수 있으며, 둘 다 제공하지 않을 수는 없습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "보안 규칙 생성 성공",
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
    ResponseEntity<Object> createSecurityRule(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "보안 그룹 ID", required = true)
            String sgId,
            @RequestBody
            @Parameter(description = "보안 그룹 생성 요청 정보", required = true)
            CreateSecurityRuleRequest request);


    @Operation(
            summary = "보안 규칙 삭제",
            description = "지정한 보안 규칙을 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "보안 규칙 삭제 성공",
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
                    description = "보안 규칙 없음 - 지정한 보안 규칙을 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content()
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteSecurityRule(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "보안 규칙 ID", required = true)
            @RequestParam String srId);

}
