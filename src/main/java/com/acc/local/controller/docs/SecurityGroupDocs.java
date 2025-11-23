package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateSecurityGroupRequest;
import com.acc.local.dto.network.ViewSecurityGroupsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/security-groups")
@Tag(name = "Security Group", description = "보안 그룹 API")
@SecurityRequirement(name = "access-token")
public interface SecurityGroupDocs {

    @Schema(name = "보안 그룹 페이지", description = "보안 그룹 페이지 응답")
    class SecurityGroupPageResponse extends PageResponse<ViewSecurityGroupsResponse> {}

    @Operation(
            summary = "보안그룹 조회",
            description = "보안 그룹 목록이나 보안 그룹의 상세 정보(보안 규칙)를 조회합니다. <br>" +
            "sgId를 제공하면 특정 보안 그룹에 속한 규칙을 조회하며, 제공하지 않을 시 보안 그룹 목록을 조회합니다. <br>" +
            "page는 sgId 제공 여부 상관없이 사용 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "보안 그룹 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                @ExampleObject(
                                        name = "보안 그룹 페이지",
                                        value = "{\"contents\": [" +
                                                "{\"securityGroupId\": \"123e4567-e89b-12d3-a456-426614174000\", " +
                                                "\"securityGroupName\": \"my-security-group\", " +
                                                "\"description\": \"This is my security group\", " +
                                                "\"createdAt\": \"2021-01-01T00:00:00Z\"}" +
                                                "], " +
                                                "\"nextMarker\": \"123e4567-e89b-12d3-a456-426614174001\", " +
                                                "\"prevMarker\": \"123e4567-e89b-12d3-a456-426614174000\", " +
                                                "\"last\": false, " +
                                                "\"first\": false, " +
                                                "\"size\": 10}"),
                                @ExampleObject(
                                        name = "보안 그룹 상세 정보",
                                        value = "{\"securityGroupId\": \"123e4567-e89b-12d3-a456-426614174000\", " +
                                                "\"securityGroupName\": \"my-security-group\", " +
                                                "\"description\": \"This is my security group\", " +
                                                "\"createdAt\": \"2021-01-01T00:00:00Z\", " +
                                                "\"rules\": {" +
                                                "\"contents\": [" +
                                                "{\"ruleId\": \"123e4567-e89b-12d3-a456-426614174001\", " +
                                                "\"direction\": \"ingress\", " +
                                                "\"protocol\": \"tcp\", " +
                                                "\"portRange\": \"80:80\", " +
                                                "\"groupId\": \"123e4567-e89b-12d3-a456-426614174000\", " +
                                                "\"prefix\": \"192.168.0.0/24\"}" +
                                                "], " +
                                                "\"first\": true, " +
                                                "\"last\": true, " +
                                                "\"size\": 1, " +
                                                "\"nextMarker\": null, " +
                                                "\"prevMarker\": null}}")
                            },
                        schema = @Schema(oneOf = {ViewSecurityGroupsResponse.class, SecurityGroupPageResponse.class})
                    )
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
    ResponseEntity<Object> viewSecurityGroups(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam(required = false)
            String sgId,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page);


    @Operation(
            summary = "보안 그룹 생성",
            description = "새로운 보안 그룹을 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "보안 그룹 생성 성공",
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
    ResponseEntity<Object> createSecurityGroup(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody
            @Parameter(description = "보안 그룹 생성 요청 정보", required = true)
            CreateSecurityGroupRequest request);


    @Operation(
            summary = "보안 그룹 삭제",
            description = "지정한 보안 그룹을 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "보안 그룹 삭제 성공",
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
                    description = "보안 그룹 없음 - 지정한 보안 그룹을 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content()
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteSecurityGroup(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "보안 그룹 ID", required = true)
            @RequestParam String sgId);

}
