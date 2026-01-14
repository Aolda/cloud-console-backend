package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.local.dto.network.CreateSecurityGroupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
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

    @Operation(
            summary = "보안 그룹 상세 조회",
            description = """
                    특정 보안 그룹의 상세 정보와 보안 규칙을 조회합니다.
                    
                    - Marker 기반 페이지네이션을 적용합니다.
                    - marker가 제공되지 않으면 첫 페이지를 조회합니다.
                    - marker는 이전 페이지의 마지막 보안 규칙 ID여야 합니다.
                    - direction이 next이면 marker 이후의 데이터를 조회합니다.
                    - direction이 prev이면 marker 이전의 데이터를 조회합니다.
                    - limit이 0이면 제한없이 모든 데이터를 조회합니다.
                    - 보안 규칙이 없으면 빈 배열을 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "보안 그룹 조회 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-BAD-REQUEST",
                                                      "message": "Neutron 보안 그룹 요청이 잘못되었습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 보안 규칙 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-RULE-BAD-REQUEST",
                                                      "message": "Neutron 보안 규칙 요청이 잘못되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-FORBIDDEN",
                                                      "message": "Neutron 보안 그룹 접근이 금지되었습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 보안 규칙 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-RULE-FORBIDDEN",
                                                      "message": "Neutron 보안 규칙 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "보안 그룹 없음 - 지정한 보안 그룹을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "보안 그룹을 찾을 수 없음",
                                            description = "지정한 ID의 보안 그룹을 찾을 수 없습니다.",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-NOT-FOUND",
                                                      "message": "Neutron 보안 그룹을 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-RETRIEVAL-FAILED",
                                                      "message": "Neutron 보안 그룹 조회에 실패했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 보안 규칙 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-RULE-RETRIEVAL-FAILED",
                                                      "message": "Neutron 보안 규칙 조회에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/{sgId}")
    ResponseEntity<Object> viewSecurityGroup(
            @Parameter(hidden = true) Authentication authentication,
            @PathVariable String sgId,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page,
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);

    @Operation(
            summary = "보안그룹 조회",
            description = """
                    보안 그룹 목록을 조회합니다.
                    
                    - Marker 기반 페이지네이션을 적용합니다.
                    - marker가 제공되지 않으면 첫 페이지를 조회합니다.
                    - marker는 이전 페이지의 마지막 보안 그룹 ID여야 합니다.
                    - direction이 next이면 marker 이후의 데이터를 조회합니다.
                    - direction이 prev이면 marker 이전의 데이터를 조회합니다.
                    - limit이 0이면 제한없이 모든 데이터를 조회합니다.
                    - 보안 그룹이 없으면 빈 배열을 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "보안 그룹 조회 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-BAD-REQUEST",
                                                      "message": "Neutron 보안 그룹 요청이 잘못되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-FORBIDDEN",
                                                      "message": "Neutron 보안 그룹 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-RETRIEVAL-FAILED",
                                                      "message": "Neutron 보안 그룹 조회에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping
    ResponseEntity<Object> viewSecurityGroups(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);


    @Operation(
            summary = "보안 그룹 생성",
            description = """
                    새로운 보안 그룹을 생성합니다.
                    
                    - default라는 이름으로 보안 그룹을 생성할 수 없습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "보안 그룹 생성 성공",
                    headers = @Header(
                            name = "Location",
                            description = "생성된 보안 그룹의 리소스 URL",
                            schema = @Schema(type = "string", example = "/api/security-groups/{sgId}")
                    ),
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "보안 그룹 이름이 유효하지 않은 경우",
                                            description = "보안 그룹 이름이 유효하지 않거나 기본 보안 그룹 이름인 경우",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-SECURITY-GROUP-NAME",
                                                      "message": "보안 그룹 이름이 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-BAD-REQUEST",
                                                      "message": "Neutron 보안 그룹 요청이 잘못되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-FORBIDDEN",
                                                      "message": "Neutron 보안 그룹 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 생성 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-CREATION-FAILED",
                                                      "message": "Neutron 보안 그룹 생성에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    ResponseEntity<Object> createSecurityGroup(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody
            @Parameter(description = "보안 그룹 생성 요청 정보", required = true)
            CreateSecurityGroupRequest request,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);


    @Operation(
            summary = "보안 그룹 삭제",
            description = """
                    지정한 보안 그룹을 삭제합니다.
                    
                    - default 보안 그룹은 삭제할 수 없습니다.
                    - 보안 그룹이 존재하지 않으면 삭제할 수 없습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "보안 그룹 삭제 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "기본 보안 그룹 삭제 시도",
                                            description = "default 보안 그룹은 삭제할 수 없습니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-SECURITY-GROUP-NAME",
                                                      "message": "보안 그룹 이름이 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-BAD-REQUEST",
                                                      "message": "Neutron 보안 그룹 요청이 잘못되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-FORBIDDEN",
                                                      "message": "Neutron 보안 그룹 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "보안 그룹 없음 - 지정한 보안 그룹을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "보안 그룹을 찾을 수 없음",
                                            description = "지정한 ID의 보안 그룹을 찾을 수 없습니다.",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-NOT-FOUND",
                                                      "message": "Neutron 보안 그룹을 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-RETRIEVAL-FAILED",
                                                      "message": "Neutron 보안 그룹 조회에 실패했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 보안 그룹 삭제 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-GROUP-DELETION-FAILED",
                                                      "message": "Neutron 보안 그룹 삭제에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteSecurityGroup(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "보안 그룹 ID", required = true)
            @RequestParam String sgId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);

}
