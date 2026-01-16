package com.acc.local.controller.docs;

import com.acc.local.dto.instance.InterfaceAttachmentRequest;
import com.acc.local.dto.instance.InterfaceAttachmentResponse;
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

import java.util.List;

@RequestMapping("/api/v1/instances/interfaces")
@Tag(name = "Instance Interface", description = "인스턴스 인터페이스 API")
@SecurityRequirement(name = "access-token")
public interface InstanceInterfaceDocs {

    @Operation(
            summary = "인터페이스 목록 조회",
            description = """
                    인스턴스에 연결된 인터페이스 목록을 조회합니다.
                    
                    - 인터페이스 ID, 네트워크 ID, MAC 주소, 인터페이스 상태 등을 확인할 수 있습니다.
                    - 고정 IP 주소 목록과 서브넷 정보를 포함합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인터페이스 목록 조회 성공"
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
                    description = "리소스 없음 - 인스턴스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "501",
                    description = "구현되지 않음 - 해당 기능이 지원되지 않음",
                    content = @Content()
            )
    })
    @GetMapping
    ResponseEntity<List<InterfaceAttachmentResponse>> listInterfaces(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "인스턴스 ID (서버 UUID)", required = true, example = "0c37a84a-c757-4f22-8c7f-0bf8b6970886")
            String instanceId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId
    );

    @Operation(
            summary = "인터페이스 연결",
            description = """
                    인스턴스에 인터페이스를 생성하고 연결합니다.
                    
                    - `interfaceId`와 `networkId`는 상호 배타적입니다.
                    - `interfaceId`를 지정하면 기존 포트를 인스턴스에 연결합니다.
                    - `networkId`를 지정하면 해당 네트워크에 새 포트를 생성하여 연결합니다.
                    - `networkId`와 함께 `fixedIps`를 지정하여 특정 IP 주소를 할당할 수 있습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "인터페이스 연결 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - interfaceId와 networkId 모두 누락되거나, networkId 없이 fixedIps만 지정됨",
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
                    description = "리소스 없음 - 인스턴스, 포트 또는 네트워크를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌 - 포트가 이미 사용 중이거나 인스턴스 상태가 적절하지 않음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "501",
                    description = "구현되지 않음 - 해당 기능이 지원되지 않음",
                    content = @Content()
            )
    })
    @PostMapping
    ResponseEntity<InterfaceAttachmentResponse> createInterface(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "인스턴스 ID (서버 UUID)", required = true, example = "0c37a84a-c757-4f22-8c7f-0bf8b6970886")
            String instanceId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "인터페이스 연결 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InterfaceAttachmentRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "인터페이스 ID로 연결",
                                            description = "기존 포트를 인스턴스에 연결",
                                            value = """
                                                    {
                                                        "interfaceId": "ce531f90-199f-48c0-816c-13e38010b442"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "네트워크 ID와 고정 IP로 연결",
                                            description = "특정 네트워크에 지정된 IP로 포트를 생성하여 연결",
                                            value = """
                                                    {
                                                        "networkId": "3cb9bc59-5699-4588-a4b1-b87f96708bc6",
                                                        "fixedIps": [
                                                            {
                                                                "ipAddress": "192.168.1.3"
                                                            }
                                                        ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @RequestBody InterfaceAttachmentRequest request
    );

    @Operation(
            summary = "인터페이스 연결 해제",
            description = """
                    인스턴스에서 인터페이스를 분리합니다.
                    
                    - 성공 시 응답 본문이 없습니다 (204 No Content).
                    - 인터페이스를 분리하면 해당 포트는 더 이상 인스턴스에 연결되지 않습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "인터페이스 연결 해제 성공",
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
                    description = "리소스 없음 - 인스턴스 또는 인터페이스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌 - 인스턴스 상태가 적절하지 않음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "501",
                    description = "구현되지 않음 - 해당 기능이 지원되지 않음",
                    content = @Content()
            )
    })
    @DeleteMapping
    ResponseEntity<Void> detachInterface(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "인스턴스 ID (서버 UUID)", required = true, example = "0c37a84a-c757-4f22-8c7f-0bf8b6970886")
            String instanceId,
            @RequestParam
            @Parameter(description = "인터페이스 ID (포트 UUID)", required = true, example = "ce531f90-199f-48c0-816c-13e38010b442")
            String interfaceId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId
    );
}

