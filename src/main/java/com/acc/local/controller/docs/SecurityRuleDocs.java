package com.acc.local.controller.docs;

import com.acc.local.dto.network.CreateSecurityRuleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
            description = """
                    새로운 보안 규칙을 생성합니다.
                    
                    - remoteSecurityGroupId와 cidr 둘 중 하나만 제공할 수 있으며, 둘 다 제공하지 않을 수는 없습니다.
                    - protocol은 프로토콜 목록을 참조하세요.
                    - direction은 ingress 또는 egress여야 합니다.
                    - port는 1-65535 범위의 숫자여야 합니다.

                    프로토콜 목록:
                    - tcp
                    - udp
                    - icmp
                    - ah
                    - dccp
                    - egp
                    - esp
                    - gre
                    - icmpv6
                    - igmp
                    - ipip
                    - ipv6-encap
                    - ipv6-frag
                    - ipv6-icmp
                    - ipv6-nonxt
                    - ipv6-opts
                    - ipv6-route
                    - ospf
                    - pgm
                    - rsvp
                    - sctp
                    - any
                    """
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
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "보안 그룹 ID가 유효하지 않은 경우",
                                            description = "보안 그룹 ID는 필수 파라미터입니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-SECURITY-GROUP-ID",
                                                      "message": "보안 그룹 ID가 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "프로토콜이 유효하지 않은 경우",
                                            description = "프로토콜은 프로토콜 목록을 참조하세요.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-SECURITY-RULE-PROTOCOL",
                                                      "message": "보안 규칙의 프로토콜이 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "방향이 유효하지 않은 경우",
                                            description = "방향은 ingress 또는 egress여야 합니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-SECURITY-RULE-DIRECTION",
                                                      "message": "보안 규칙의 방향이 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "포트 범위가 유효하지 않은 경우",
                                            description = "포트는 1-65535 범위의 숫자여야 합니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-SECURITY-RULE-PORT-RANGE",
                                                      "message": "보안 규칙의 포트 범위가 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "보안 그룹 ID 또는 CIDR이 유효하지 않은 경우",
                                            description = "remoteSecurityGroupId와 cidr 둘 중 하나만 제공해야 합니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-SECURITY-RULE-SECURITY-GROUP-ID-OR-CIDR",
                                                      "message": "보안 규칙의 보안 그룹 ID 또는 CIDR이 유효하지 않습니다."
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
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 보안 규칙 생성 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-RULE-CREATION-FAILED",
                                                      "message": "Neutron 보안 규칙 생성에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    ResponseEntity<Object> createSecurityRule(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody
            @Parameter(description = "보안 그룹 생성 요청 정보", required = true)
            CreateSecurityRuleRequest request,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);


    @Operation(
            summary = "보안 규칙 삭제",
            description = """
                    지정한 보안 규칙을 삭제합니다.
                    
                    - 보안 규칙이 존재하지 않으면 삭제할 수 없습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "보안 규칙 삭제 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
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
                    description = "보안 규칙 없음 - 지정한 보안 규칙을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "보안 규칙을 찾을 수 없음",
                                            description = "지정한 ID의 보안 규칙을 찾을 수 없습니다.",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-RULE-NOT-FOUND",
                                                      "message": "Neutron 보안 규칙을 찾을 수 없습니다."
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
                                            name = "오픈스택 보안 규칙 삭제 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SECURITY-RULE-DELETION-FAILED",
                                                      "message": "Neutron 보안 규칙 삭제에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteSecurityRule(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "보안 규칙 ID", required = true)
            @RequestParam String srId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);

}
