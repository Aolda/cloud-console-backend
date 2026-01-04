package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.type.InstanceTypeCreateRequest;
import com.acc.local.dto.type.InstanceTypeResponse;
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

@Tag(name = "InstanceType", description = "인스턴스 타입(Flavor) API")
@SecurityRequirement(name = "access-token")
@RequestMapping("/api/v1")
public interface InstanceTypeDocs {

    @Operation(
            summary = "[관리자] 인스턴스 타입 생성",
            description = "새로운 인스턴스 타입(Flavor)을 생성합니다.\n\n"
                    + "**필수 정보**\n"
                    + "- 타입 이름 (typeName)\n"
                    + "- 아키텍처 (architect): X86 또는 HETEROGENEOUS\n"
                    + "- 목적 (purpose): GENERAL, COMPUTE, MEMORY 등\n"
                    + "- vCPU 코어 수 (core)\n"
                    + "- 메모리 크기 (ram): MiB 단위\n"
                    + "- 루트 디스크 크기 (diskSize): GiB 단위\n\n"
                    + "**선택 정보**\n"
                    + "- 내부 네트워크 대역폭 (bandwidth): Gbps 단위\n"
                    + "- NUMA 노드 수 (numa)\n"
                    + "- USB 장치 허용 여부 (usb)\n\n"
                    + "**참고**\n"
                    + "- OpenStack Flavor로 생성됩니다\n"
                    + "- 확장 속성(extra_specs)으로 아키텍처, 목적, USB 지원 여부가 저장됩니다"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "인스턴스 타입 생성 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 타입 이름 형식 오류 또는 필수 파라미터 누락",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "타입 이름 형식 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-INSTANCE-TYPE-INVALID-NAME",
                                                      "message": "인스턴스 타입 이름이 유효하지 않습니다."
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
                    description = "권한 없음 - 관리자 권한 필요",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack Flavor 생성 실패",
                    content = @Content()
            )
    })
    @PostMapping("/admin/types")
    ResponseEntity<Object> createInstanceType(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody
            @Parameter(description = "인스턴스 타입 생성 요청 정보", required = true)
            InstanceTypeCreateRequest request);


    @Operation(
            summary = "[관리자] 인스턴스 타입 목록 조회",
            description = "관리자가 인스턴스 타입 목록을 조회합니다.\n\n"
                    + "**쿼리 파라미터**\n"
                    + "- architect: 아키텍처 필터 (X86, HETEROGENEOUS)\n"
                    + "- marker: 페이지네이션 경계 ID (첫 조회 시 null)\n"
                    + "- direction: next(기본, 다음 페이지) 또는 prev(이전 페이지)\n"
                    + "- limit: 페이지 크기 (기본 10, 전체 조회는 0)\n\n"
                    + "**페이지네이션**\n"
                    + "- Marker 기반 페이지네이션 사용\n"
                    + "- next: id > marker 오름차순\n"
                    + "- prev: id < marker 내림차순으로 조회 후 뒤집어 반환\n\n"
                    + "**예시 쿼리**\n"
                    + "- 첫 페이지: GET /api/v1/admin/types?limit=10\n"
                    + "- 다음 페이지: GET /api/v1/admin/types?marker=flavor-id-10&direction=next&limit=10\n"
                    + "- 이전 페이지: GET /api/v1/admin/types?marker=flavor-id-10&direction=prev&limit=10"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 관리자 권한 필요",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack 호출 실패",
                    content = @Content()
            )
    })
    @GetMapping("/admin/types")
    ResponseEntity<PageResponse<InstanceTypeResponse>> getAdminInstanceTypes(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "페이지 정보 (Marker 기반)", required = false)
            PageRequest page,
            @Parameter(description = "아키텍처 필터 (X86 또는 HETEROGENEOUS)", required = false, example = "X86")
            @RequestParam(required = false)
            String architect);


    @Operation(
            summary = "인스턴스 타입 목록 조회",
            description = "사용자가 생성 가능한 인스턴스 타입 목록을 조회합니다.\n\n"
                    + "**조회 범위**\n"
                    + "- Public 타입: 모든 사용자가 조회 가능\n"
                    + "- Private 타입: 권한이 있는 사용자만 조회 가능\n\n"
                    + "**쿼리 파라미터**\n"
                    + "- projectId: 프로젝트 ID (필수)\n"
                    + "- architect: 아키텍처 필터 (X86, HETEROGENEOUS)\n"
                    + "- marker: 페이지네이션 경계 ID (첫 조회 시 null)\n"
                    + "- direction: next(기본, 다음 페이지) 또는 prev(이전 페이지)\n"
                    + "- limit: 페이지 크기 (기본 10, 전체 조회는 0)\n\n"
                    + "**페이지네이션**\n"
                    + "- Marker 기반 페이지네이션 사용\n"
                    + "- next: id > marker 오름차순\n"
                    + "- prev: id < marker 내림차순으로 조회 후 뒤집어 반환\n\n"
                    + "**예시 쿼리**\n"
                    + "- 첫 페이지: GET /api/v1/types?projectId=project-uuid-1234&limit=10\n"
                    + "- 다음 페이지: GET /api/v1/types?projectId=project-uuid-1234&marker=flavor-id-10&direction=next&limit=10\n"
                    + "- 이전 페이지: GET /api/v1/types?projectId=project-uuid-1234&marker=flavor-id-10&direction=prev&limit=10"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
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
                    description = "리소스 없음 - 프로젝트를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "프로젝트 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-PROJECT-NOT-FOUND",
                                                      "message": "프로젝트를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack 호출 실패",
                    content = @Content()
            )
    })
    @GetMapping("/types")
    ResponseEntity<PageResponse<InstanceTypeResponse>> getUserInstanceTypes(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "페이지 정보 (Marker 기반)", required = false)
            PageRequest page,
            @Parameter(description = "아키텍처 필터 (X86 또는 HETEROGENEOUS)", required = false, example = "X86")
            @RequestParam(required = false)
            String architect,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true, example = "project-uuid-1234")
            String projectId);
}
