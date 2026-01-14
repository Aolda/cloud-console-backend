package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateSubnetRequest;
import com.acc.local.dto.network.ViewSubnetsResponse;
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

@RequestMapping("/api/v1/subnets")
@Tag(name = "Subnet", description = "서브넷 API")
@SecurityRequirement(name = "access-token")
public interface SubnetDocs {

    @Operation(
            summary = "서브넷 목록 조회",
            description = """
                    특정 네트워크에 속한 서브넷 목록을 조회합니다.
                    
                    - Marker 기반 페이지네이션을 적용합니다.
                    - marker가 제공되지 않으면 첫 페이지를 조회합니다.
                    - marker는 이전 페이지의 마지막 서브넷 ID여야 합니다.
                    - direction이 next이면 marker 이후의 데이터를 조회합니다.
                    - direction이 prev이면 marker 이전의 데이터를 조회합니다.
                    - limit이 0이면 제한없이 모든 데이터를 조회합니다.
                    - 서브넷이 없으면 빈 배열을 반환합니다.
                    - limit: 한 번에 조회할 서브넷 수 (0: 제한없음)
                    - marker: 이전 페이지의 마지막 서브넷 ID
                    - direction: 페이지네이션 방향 (next, prev)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "서브넷 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 서브넷 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-BAD-REQUEST",
                                                      "message": "Neutron 서브넷 요청이 잘못되었습니다."
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
                                            name = "오픈스택 서브넷 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-FORBIDDEN",
                                                      "message": "Neutron 서브넷 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "네트워크 없음 - 지정한 네트워크를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 서브넷을 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-NOT-FOUND",
                                                      "message": "Neutron 서브넷을 찾을 수 없습니다."
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
                                            name = "오픈스택 서브넷 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-RETRIEVAL-FAILED",
                                                      "message": "Neutron 서브넷 조회에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<ViewSubnetsResponse>> viewSubnets(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page,
            @Parameter(description = "네트워크 ID 필터", required = true, example = "network-1234")
            @RequestParam(required = true) String networkId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId
    );


    @Operation(
            summary = "서브넷 상세 조회",
            description = """
                    서브넷 ID로 특정 서브넷의 상세 정보를 조회합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "서브넷 상세 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 서브넷 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-BAD-REQUEST",
                                                      "message": "Neutron 서브넷 요청이 잘못되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 서브넷 인증 실패",
                                            value = """
                                                    {
                                                      "status": 401,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-UNAUTHORIZED",
                                                      "message": "Neutron 서브넷 접근이 인증되지 않았습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 서브넷 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-FORBIDDEN",
                                                      "message": "Neutron 서브넷 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "서브넷 없음 - 지정한 서브넷을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "서브넷을 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NOT-FOUND-SUBNET",
                                                      "message": "해당 서브넷이 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 서브넷을 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-NOT-FOUND",
                                                      "message": "Neutron 서브넷을 찾을 수 없습니다."
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
                                            name = "오픈스택 서브넷 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-RETRIEVAL-FAILED",
                                                      "message": "Neutron 서브넷 조회에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/{subnetId}")
    ResponseEntity<ViewSubnetsResponse> getSubnet(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "서브넷 ID", required = true, example = "subnet-1234")
            @RequestParam String subnetId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId
    );


    @Operation(
            summary = "서브넷 생성",
            description = """
                    네트워크 하위에 새로운 서브넷을 생성합니다.
                    
                    - 게이트웨이 IP를 지정하지 않을 시 기본 게이트웨이 IP가 자동 할당됩니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "서브넷 생성 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "서브넷 이름이 유효하지 않은 경우",
                                            description = "서브넷 이름에는 영문자, 숫자, '-', '_'만 사용할 수 있습니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-SUBNET-NAME",
                                                      "message": "서브넷 이름이 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "서브넷 CIDR가 유효하지 않은 경우",
                                            description = "서브넷 CIDR 값이 유효하지 않습니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-SUBNET-CIDR",
                                                      "message": "서브넷 CIDR 값이 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "서브넷 게이트웨이 IP가 유효하지 않은 경우",
                                            description = "서브넷 게이트웨이 IP가 유효하지 않습니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-SUBNET-GATEWAY-IP",
                                                      "message": "서브넷 게이트웨이 IP가 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 서브넷 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-BAD-REQUEST",
                                                      "message": "Neutron 서브넷 요청이 잘못되었습니다."
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
                                            name = "오픈스택 서브넷 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-FORBIDDEN",
                                                      "message": "Neutron 서브넷 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "네트워크 없음 - 지정한 네트워크를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 서브넷을 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-NOT-FOUND",
                                                      "message": "Neutron 서브넷을 찾을 수 없습니다."
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
                                            name = "오픈스택 서브넷 생성 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-CREATION-FAILED",
                                                      "message": "Neutron 서브넷 생성에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    ResponseEntity<Object> createSubnet(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody
            @Parameter(description = "서브넷 생성 요청 정보", required = true)
            CreateSubnetRequest request,
            @RequestParam
            @Parameter(description = "네트워크 ID", required = true)
            String networkId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId
    );

    @Operation(
            summary = "서브넷 삭제",
            description = """
                    서브넷 ID로 특정 서브넷을 삭제합니다.
                    
                    - default network의 default-subnet은 삭제할 수 없습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "서브넷 삭제 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 서브넷 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-BAD-REQUEST",
                                                      "message": "Neutron 서브넷 요청이 잘못되었습니다."
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
                    description = "권한 없음 - 프로젝트 접근 권한이 없거나 서브넷 삭제 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "서브넷 삭제 불가",
                                            description = "해당 서브넷은 삭제할 수 없습니다.",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-CAN-NOT-DELETE-SUBNET",
                                                      "message": "해당 서브넷은 삭제할 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 서브넷 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-FORBIDDEN",
                                                      "message": "Neutron 서브넷 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "서브넷 없음 - 지정한 서브넷을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "서브넷을 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NOT-FOUND-SUBNET",
                                                      "message": "해당 서브넷이 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 서브넷을 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-NOT-FOUND",
                                                      "message": "Neutron 서브넷을 찾을 수 없습니다."
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
                                            name = "오픈스택 서브넷 삭제 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-SUBNET-DELETION-FAILED",
                                                      "message": "Neutron 서브넷 삭제에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteSubnet(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "서브넷 ID", required = true)
            String subnetId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId
    );

}
