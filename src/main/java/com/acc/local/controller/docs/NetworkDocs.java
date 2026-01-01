package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateNetworkRequest;
import com.acc.local.dto.network.ViewNetworksResponse;
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

@RequestMapping("/api/v1/networks")
@Tag(name = "Network", description = "네트워크 API")
@SecurityRequirement(name = "access-token")
public interface NetworkDocs {

    @Operation(
            summary = "네트워크 조회",
            description = """
                    프로젝트에 속한 네트워크 목록을 조회합니다.
                    
                    - Marker 기반 페이지네이션을 적용합니다.
                    - marker가 제공되지 않으면 첫 페이지를 조회합니다.
                    - marker는 이전 페이지의 마지막 네트워크 ID여야 합니다.
                    - direction이 next이면 marker 이후의 데이터를 조회합니다.
                    - direction이 prev이면 marker 이전의 데이터를 조회합니다.
                    - limit이 0이면 제한없이 모든 데이터를 조회합니다.
                    - 네트워크가 없으면 빈 배열을 반환합니다.
                    - limit: 한 번에 조회할 네트워크 수 (0: 제한없음)
                    - marker: 이전 페이지의 마지막 네트워크 ID
                    - direction: 페이지네이션 방향 (next, prev)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "네트워크 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 네트워크 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-BAD-REQUEST",
                                                      "message": "Neutron 네트워크 요청이 잘못되었습니다."
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
                                            name = "오픈스택 네트워크 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-FORBIDDEN",
                                                      "message": "Neutron 네트워크 접근이 금지되었습니다."
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
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 네트워크 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-RETRIEVAL-FAILED",
                                                      "message": "Neutron 네트워크 조회에 실패했습니다."
                                                    }
                                                    """
                                    ),
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
    ResponseEntity<PageResponse<ViewNetworksResponse>> viewNetworks(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page);


    @Operation(
            summary = "네트워크 생성",
            description = "새로운 네트워크와 서브넷을 생성합니다. \n\n" +
                    "- 서브넷은 필수 요청이 아닙니다.\n" +
                    "- 서브넷 정보가 제공되지 않으면 네트워크만 생성됩니다.\n" +
                    "- default-network의 이름으로 네트워크를 생성할 수 없습니다.\n" +
                    "- 네트워크, 서브넷의 이름은 중복이 가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "네트워크 및 서브넷 생성 성공. Location 헤더에 생성된 네트워크의 URI가 반환됩니다.",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                        mediaType = "application/json",
                        examples = {
                                @ExampleObject(
                                        name = "잘못된 네트워크 이름",
                                        value = """
                                                {
                                                    "status": 400,
                                                    "code": "ACC-NETWORK-INVALID-NETWORK-NAME",
                                                    "message": "네트워크 이름이 유효하지 않습니다."
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "잘못된 MTU 값",
                                        value = """
                                                {
                                                    "status": 400,
                                                    "code": "ACC-NETWORK-INVALID-NETWORK-MTU",
                                                    "message": "네트워크 MTU 값이 유효하지 않습니다."
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "잘못된 서브넷 이름",
                                        value = """
                                                {
                                                    "status": 400,
                                                    "code": "ACC-NETWORK-INVALID-SUBNET-NAME",
                                                    "message": "서브넷 이름이 유효하지 않습니다."
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "잘못된 서브넷 CIDR",
                                        value = """
                                                {
                                                    "status": 400,
                                                    "code": "ACC-NETWORK-INVALID-SUBNET-CIDR",
                                                    "message": "서브넷 CIDR 값이 유효하지 않습니다."
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "겹치는 서브넷 CIDR",
                                        value = """
                                                {
                                                    "status": 400,
                                                    "code": "ACC-NETWORK-OVERLAPPING-SUBNET-CIDR",
                                                    "message": "서브넷 CIDR 값이 서로 겹칩니다."
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "오픈스택 네트워크 요청 오류",
                                        value = """
                                                {
                                                    "status": 400,
                                                    "code": "ACC-NETWORK-NEUTRON-NETWORK-BAD-REQUEST",
                                                    "message": "Neutron 네트워크 요청이 잘못되었습니다."
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
                                            name = "오픈스택 네트워크 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-FORBIDDEN",
                                                      "message": "Neutron 네트워크 접근이 금지되었습니다."
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
                    description = "네트워크 없음 - 서브넷 생성 시 네트워크를 찾을 수 없음",
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
                                            name = "오픈스택 네트워크 생성 실패",
                                            value = """
                                                    {
                                                        "status": 500,
                                                        "code": "ACC-NETWORK-NEUTRON-NETWORK-CREATION-FAILED",
                                                        "message": "오픈스택 네트워크 생성에 실패했습니다."
                                                    }
                                                    """
                                    ),
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
    ResponseEntity<Object> createNetwork(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId,
            @RequestBody
            @Parameter(description = "네트워크 생성 요청 정보", required = true)
            CreateNetworkRequest request);


    @Operation(
            summary = "네트워크 삭제",
            description = """
                    지정한 네트워크를 삭제합니다.
                    
                    - default-network는 삭제할 수 없습니다.
                    - SSH 포트포워딩은 자동으로 해제되지 않습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "네트워크 삭제 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 네트워크 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-BAD-REQUEST",
                                                      "message": "Neutron 네트워크 요청이 잘못되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음 또는 기본 네트워크 삭제 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "기본 네트워크 삭제 불가",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-CAN-NOT-DELETE-NETWORK",
                                                      "message": "해당 네트워크는 삭제할 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 네트워크 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-FORBIDDEN",
                                                      "message": "Neutron 네트워크 접근이 금지되었습니다."
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
                                            name = "네트워크를 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NOT-FOUND-NETWORK",
                                                      "message": "해당 네트워크가 존재하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 네트워크를 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-NOT-FOUND",
                                                      "message": "Neutron 네트워크를 찾을 수 없습니다."
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
                                            name = "오픈스택 네트워크 삭제 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-DELETION-FAILED",
                                                      "message": "Neutron 네트워크 삭제에 실패했습니다"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 네트워크 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-NETWORK-RETRIEVAL-FAILED",
                                                      "message": "Neutron 네트워크 조회에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteNetwork(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId,
            @RequestParam String networkId);
}
