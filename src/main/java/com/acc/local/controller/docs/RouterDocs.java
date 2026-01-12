package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateRouterRequest;
import com.acc.local.dto.network.ViewRoutersResponse;
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

@RequestMapping("/api/v1/routers")
@Tag(name = "Router", description = "라우터 API")
@SecurityRequirement(name = "access-token")
public interface RouterDocs {

    @Operation(
            summary = "라우터 조회",
            description = """
                    라우터 목록을 조회합니다.
                    
                    - Marker 기반 페이지네이션을 적용합니다.
                    - marker가 제공되지 않으면 첫 페이지를 조회합니다.
                    - marker는 이전 페이지의 마지막 라우터 ID여야 합니다.
                    - direction이 next이면 marker 이후의 데이터를 조회합니다.
                    - direction이 prev이면 marker 이전의 데이터를 조회합니다.
                    - limit이 0이면 제한없이 모든 데이터를 조회합니다.
                    - 라우터 없으면 빈 배열을 반환합니다.
                    - limit: 한 번에 조회할 라우터 수 (0: 제한없음)
                    - marker: 이전 페이지의 마지막 라우터 ID
                    - direction: 페이지네이션 방향 (next, prev)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "라우터 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 라우터 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-BAD-REQUEST",
                                                      "message": "Neutron 라우터 요청이 잘못되었습니다."
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
                                            name = "오픈스택 라우터 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-FORBIDDEN",
                                                      "message": "Neutron 라우터 접근이 금지되었습니다."
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
                                            name = "오픈스택 라우터 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-RETRIEVAL-FAILED",
                                                      "message": "Neutron 라우터 조회에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<ViewRoutersResponse>> viewRouters(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);

    @Operation(
            summary = "라우터 생성",
            description = """
                    새로운 라우터를 생성합니다.
                    
                    - 요청 시 라우터 이름과 외부 네트워크 연결 여부를 지정할 수 있습니다.
                    - default-router라는 이름으로 라우터를 생성할 수 없습니다.
                    - 라우터 이름은 중복 가능합니다.
                    """
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
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "라우터 이름이 'default-router'인 경우",
                                            description = "라우터 이름은 'default-router'일 수 없습니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-ROUTER-NAME",
                                                      "message": "라우터 이름이 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "라우터 이름에 허용되지 않는 문자가 포함된 경우",
                                            description = "라우터 이름에는 영문자, 숫자, '-', '_'만 사용할 수 있습니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-ROUTER-NAME",
                                                      "message": "라우터 이름이 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "외부 네트워크 연결 여부가 유효하지 않은 경우",
                                            description = "isExternal 필드는 true 또는 false여야 합니다.",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-INVALID-ROUTER-GATEWAY",
                                                      "message": "라우터 게이트웨이 설정이 유효하지 않습니다."
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
                                            name = "오픈스택 라우터 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-BAD-REQUEST",
                                                      "message": "Neutron 라우터 요청이 잘못되었습니다."
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
                                            name = "오픈스택 라우터 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-FORBIDDEN",
                                                      "message": "Neutron 라우터 접근이 금지되었습니다."
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
                                            name = "오픈스택 라우터 생성 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-CREATION-FAILED",
                                                      "message": "Neutron 라우터 생성에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    ResponseEntity<Object> createRouter(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody
            @Parameter(description = "라우터 생성 요청 정보", required = true)
            CreateRouterRequest request,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);

    @Operation(
            summary = "라우터 삭제",
            description = """
                    지정한 라우터를 삭제합니다.
                    
                    - default-router는 삭제할 수 없습니다.
                    """
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
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 라우터 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-BAD-REQUEST",
                                                      "message": "Neutron 라우터 요청이 잘못되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 프로젝트 접근 권한이 없음 또는 기본 라우터 삭제 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "기본 라우터 삭제 시도",
                                            description = "default-router는 삭제할 수 없습니다.",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-CAN-NOT-DELETE-ROUTER",
                                                      "message": "해당 라우터는 삭제할 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 라우터 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-FORBIDDEN",
                                                      "message": "Neutron 라우터 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "라우터 없음 - 지정한 라우터를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 라우터 삭제 시도",
                                            description = "지정한 ID의 라우터를 찾을 수 없습니다.",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NOT-FOUND-ROUTER",
                                                      "message": "해당 라우터가 존재하지 않습니다."
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
                                            name = "오픈스택 라우터 삭제 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-DELETION-FAILED",
                                                      "message": "Neutron 라우터 삭제에 실패했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 라우터 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-RETRIEVAL-FAILED",
                                                      "message": "Neutron 라우터 조회에 실패했습니다."
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
            @RequestParam String routerId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);

    @Operation(
            summary = "라우터-서브넷 연결",
            description = """
                    지정한 라우터를 지정한 서브넷에 연결합니다.
                    
                    - 같은 서브넷에 대해서는 중복 연결되지 않습니다.
                    - CIDR이 같은 서브넷에 대해서는 연결되지 않습니다.
                    - 서브넷의 게이트웨이 라우터가 없다면 자동으로 게이트웨이 라우터로 설정됩니다.
                    - 서브넷에 이미 게이트웨이 라우터가 존재한다면 새로운 포트로 라우터 인터페이스를 생성하여 연결합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "라우터-서브넷 연결 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 라우터 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-BAD-REQUEST",
                                                      "message": "Neutron 라우터 요청이 잘못되었습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 포트 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-PORT-BAD-REQUEST",
                                                      "message": "Neutron 포트 요청이 잘못되었습니다."
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
                                            name = "오픈스택 라우터 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-FORBIDDEN",
                                                      "message": "Neutron 라우터 접근이 금지되었습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 포트 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-PORT-FORBIDDEN",
                                                      "message": "Neutron 포트 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "라우터, 서브넷 또는 네트워크 리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "라우터를 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-NOT-FOUND",
                                                      "message": "Neutron 라우터를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
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
                                    ),
                                    @ExampleObject(
                                            name = "네트워크 리소스를 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-PORT-NETWORK-RESOURCE-NOT-FOUND",
                                                      "message": "Neutron 포트의 네트워크 관련 리소스를 찾을 수 없습니다."
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
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 포트 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-PORT-RETRIEVAL-FAILED",
                                                      "message": "Neutron 포트 조회에 실패했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 포트 생성 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-PORT-CREATION-FAILED",
                                                      "message": "Neutron 포트 생성에 실패했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 라우터-서브넷 연결 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-CONNECT-SUBNET-FAILED",
                                                      "message": "Neutron 라우터와 서브넷 연결에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PutMapping("/connect-subnet")
    ResponseEntity<Object> connectRouterToSubnet(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "연결할 라우터 ID", required = true) String routerId,
            @RequestParam
            @Parameter(description = "연결할 서브넷 ID", required = true) String subnetId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);

    @Operation(
            summary = "라우터-서브넷 연결 해제",
            description = """
                    지정한 라우터를 지정한 서브넷에서 연결 해제합니다.
                    
                    - default-router에서 default-network의 default-subnet은 해제 할 수 없습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "라우터-서브넷 연결 해제 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "오픈스택 라우터 요청 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-BAD-REQUEST",
                                                      "message": "Neutron 라우터 요청이 잘못되었습니다."
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
                    description = "권한 없음 - 프로젝트 접근 권한이 없거나 기본 서브넷 연결 해제 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "기본 서브넷 연결 해제 불가",
                                            description = "default-router에서 default-network의 default-subnet은 해제할 수 없습니다.",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-CAN-NOT-DISCONNECT-ROUTER-FROM-SUBNET",
                                                      "message": "해당 서브넷은 라우터에서 연결 해제할 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 라우터 접근 금지",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-FORBIDDEN",
                                                      "message": "Neutron 라우터 접근이 금지되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "라우터 또는 서브넷 없음 - 지정한 라우터 또는 서브넷을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "라우터를 찾을 수 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-NOT-FOUND",
                                                      "message": "Neutron 라우터를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
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
                                            name = "오픈스택 라우터 조회 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-RETRIEVAL-FAILED",
                                                      "message": "Neutron 라우터 조회에 실패했습니다."
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
                                    ),
                                    @ExampleObject(
                                            name = "오픈스택 라우터-서브넷 연결 해제 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-NETWORK-NEUTRON-ROUTER-DISCONNECT-SUBNET-FAILED",
                                                      "message": "Neutron 라우터와 서브넷 연결 해제에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PutMapping("/disconnect-subnet")
    ResponseEntity<Object> disconnectRouterFromSubnet(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "연결 해제할 라우터 ID", required = true) String routerId,
            @RequestParam
            @Parameter(description = "연결 해제할 서브넷 ID", required = true) String subnetId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId);
}
