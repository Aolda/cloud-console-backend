package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.network.CreateInterfaceRequest;
import com.acc.local.dto.network.ViewInterfacesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/interfaces")
@Tag(name = "Interface", description = "인터페이스 API")
public interface InterfaceDocs {

    @Operation(
            summary = "인터페이스 목록 조회",
            description = "프로젝트에 속한 인터페이스 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인터페이스 목록 조회 성공",
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
    ResponseEntity<PageResponse<ViewInterfacesResponse>> viewInterfaces(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page,
            @Parameter(description = "인스턴스 ID 필터", required = false, example = "instance-1234")
            @RequestParam(required = false) String instanceId,
            @Parameter(description = "네트워크 ID 필터", required = false, example = "network-1234")
            @RequestParam(required = false) String networkId);


    @Operation(
            summary = "인터페이스 생성",
            description = "새로운 인터페이스를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "인터페이스 생성 성공",
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
    ResponseEntity<Object> createInterface(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody
            @Parameter(description = "인터페이스 생성 요청 정보", required = true)
            CreateInterfaceRequest request);


    @Operation(
            summary = "인터페이스 삭제",
            description = "지정한 인터페이스를 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "인터페이스 삭제 성공",
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
                    description = "인터페이스 없음 - 지정한 인터페이스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content()
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteInterface(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam String interfaceId);

    @Operation(
            summary = "External IP 할당",
            description = "지정한 인터페이스에 External IP를 할당합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "IP 할당 성공",
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
    @PostMapping("/external-ip")
    ResponseEntity<Object> allocateExternalIp(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "인터페이스 ID", required = true, example = "interface-1234")
            String interfaceId);

    @Operation(
            summary = "External IP 해제",
            description = "지정한 인터페이스의 External IP를 해제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "IP 해제 성공",
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
                    description = "인터페이스 없음 - 지정한 인터페이스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 호출 오류",
                    content = @Content()
            )
    })
    @DeleteMapping(path = "/external-ip")
    ResponseEntity<Object> releaseExternalIp(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "인터페이스 ID", required = true, example = "interface-1234")
            String interfaceId);

    @Operation(
            summary = "SSH 포트포워딩 설정",
            description = "지정한 인터페이스에 포트포워딩을 설정합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "포트포워딩 설정 성공",
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
                    responseCode = "404",
                    description = "인터페이스 없음 - 지정한 인터페이스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 혹은 APM 호출 오류",
                    content = @Content()
            )
    })
    @PostMapping("/forwarding" )
    ResponseEntity<Object> createPortForwarding(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "인터페이스 ID", required = true, example = "interface-1234")
            String interfaceId);

    @Operation(
            summary = "SSH 포트포워딩 해제",
            description = "지정한 인터페이스의 포트포워딩을 해제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "포트포워딩 해제 성공",
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
                    description = "인터페이스 없음 - 지정한 인터페이스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 오픈스택 혹은 APM 호출 오류",
                    content = @Content()
            )
    })
    @DeleteMapping("/forwarding" )
    ResponseEntity<Object> deletePortForwarding(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam
            @Parameter(description = "인터페이스 ID", required = true, example = "interface-1234")
            String interfaceId);
}
