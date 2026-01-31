package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
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

@Tag(name = "Volume", description = "볼륨 API")
@RequestMapping("/api/v1/volumes")
@SecurityRequirement(name = "access-token")
public interface VolumeDocs {

    @Operation(
            summary = "볼륨 목록 조회",
            description = "특정 프로젝트에 속한 볼륨 목록을 페이지네이션 방식으로 조회합니다.\n\n"
                    + "**페이지네이션 (마커 기반)**\n"
                    + "- marker: 이전 조회의 경계 ID (첫 조회 시 null)\n"
                    + "- direction: next(기본, 다음 페이지) | prev(이전 페이지)\n"
                    + "- limit: 페이지 크기 (기본 10, 전체 조회는 0)\n\n"
                    + "**동작 방식**\n"
                    + "- next: id > marker 기준 오름차순 조회\n"
                    + "- prev: id < marker 기준으로 조회 후 역순 정렬하여 반환\n\n"
                    + "**예시 쿼리**\n"
                    + "- 첫 페이지: GET /api/v1/volumes?projectId=xxx&limit=10\n"
                    + "- 다음 페이지: GET /api/v1/volumes?projectId=xxx&marker=lastId&direction=next&limit=10\n"
                    + "- 이전 페이지: GET /api/v1/volumes?projectId=xxx&marker=firstId&direction=prev&limit=10\n"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "볼륨 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Object.class),
                            examples = {
                                    @ExampleObject(
                                            name = "볼륨 목록 조회 성공",
                                            value = """
                                                    {
                                                      "contents": [
                                                        {
                                                          "volumeId": "e16fe0b5-2e01-4ad3-9b7a-49b717790523",
                                                          "name": "",
                                                          "size": 0,
                                                          "status": null,
                                                          "volumeType": null,
                                                          "description": null,
                                                          "availabilityZone": null,
                                                          "createdAt": null,
                                                          "bootable": null
                                                        },
                                                        {
                                                          "volumeId": "0cf6b0a7-fd42-40a3-94e4-6306ddaf4101",
                                                          "name": "",
                                                          "size": 0,
                                                          "status": null,
                                                          "volumeType": null,
                                                          "description": null,
                                                          "availabilityZone": null,
                                                          "createdAt": null,
                                                          "bootable": null
                                                        },
                                                        {
                                                          "volumeId": "2d0771bc-d8ca-4fec-998c-0afd6b8b73a7",
                                                          "name": "",
                                                          "size": 0,
                                                          "status": null,
                                                          "volumeType": null,
                                                          "description": null,
                                                          "availabilityZone": null,
                                                          "createdAt": null,
                                                          "bootable": null
                                                        },
                                                        {
                                                          "volumeId": "2630a240-37f3-4a17-b5ba-6b79732a9d64",
                                                          "name": "",
                                                          "size": 0,
                                                          "status": null,
                                                          "volumeType": null,
                                                          "description": null,
                                                          "availabilityZone": null,
                                                          "createdAt": null,
                                                          "bootable": null
                                                        },
                                                        {
                                                          "volumeId": "97c32fe5-4f31-4cbd-ab77-e4aa38f4a726",
                                                          "name": "",
                                                          "size": 0,
                                                          "status": null,
                                                          "volumeType": null,
                                                          "description": null,
                                                          "availabilityZone": null,
                                                          "createdAt": null,
                                                          "bootable": null
                                                        },
                                                        {
                                                          "volumeId": "1ecd9a92-0728-40c7-9087-6d6af8335424",
                                                          "name": "",
                                                          "size": 0,
                                                          "status": null,
                                                          "volumeType": null,
                                                          "description": null,
                                                          "availabilityZone": null,
                                                          "createdAt": null,
                                                          "bootable": null
                                                        },
                                                        {
                                                          "volumeId": "3b22407b-db0d-4bed-970f-2a5db7054c54",
                                                          "name": "",
                                                          "size": 0,
                                                          "status": null,
                                                          "volumeType": null,
                                                          "description": null,
                                                          "availabilityZone": null,
                                                          "createdAt": null,
                                                          "bootable": null
                                                        },
                                                        {
                                                          "volumeId": "ac7b4d74-f1cf-4df9-a42b-a288d5c4282c",
                                                          "name": "",
                                                          "size": 0,
                                                          "status": null,
                                                          "volumeType": null,
                                                          "description": null,
                                                          "availabilityZone": null,
                                                          "createdAt": null,
                                                          "bootable": null
                                                        },
                                                        {
                                                          "volumeId": "a84262d3-73e0-46ee-8969-c7d0b24240bb",
                                                          "name": "",
                                                          "size": 0,
                                                          "status": null,
                                                          "volumeType": null,
                                                          "description": null,
                                                          "availabilityZone": null,
                                                          "createdAt": null,
                                                          "bootable": null
                                                        }
                                                      ],
                                                      "first": true,
                                                      "last": true,
                                                      "size": 9
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
                                            name = "ACC-STORAGE-FORBIDDEN",
                                            value = """
                                                    {
                                                        "status": 403,
                                                        "code": "ACC-STORAGE-FORBIDDEN",
                                                        "message": "해당 프로젝트의 스토리지 리소스에 접근할 권한이 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "볼륨을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-VOLUME-NOT-FOUND",
                                            value = """
                                                    {
                                                        "status": 404,
                                                        "code": "ACC-STORAGE-VOLUME-NOT-FOUND",
                                                        "message": "요청한 볼륨 ID를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 - Cinder API 통신 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-CINDER-API-FAILURE",
                                            value = """
                                                    {
                                                        "status": 500,
                                                        "code": "ACC-STORAGE-CINDER-API-FAILURE",
                                                        "message": "OpenStack Cinder API 통신 중 오류가 발생하였습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<VolumeResponse>> getVolumes(
            @ModelAttribute
            PageRequest page,
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId
    );

    @Operation(
            summary = "볼륨 단건 조회",
            description = "특정 프로젝트에 속한 특정 볼륨 목록 단건 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "볼륨 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VolumeResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "볼륨 단건 조회",
                                            value = """
                                                    {
                                                      "volumeId": "e16fe0b5-2e01-4ad3-9b7a-49b717790523",
                                                      "name": "",
                                                      "size": 10,
                                                      "status": "in-use",
                                                      "volumeType": "__DEFAULT__",
                                                      "description": "",
                                                      "availabilityZone": "nova",
                                                      "createdAt": "2026-01-05T11:03:26.000000",
                                                      "bootable": "true"
                                                    }
                                                    """
                                    )

                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-VOLUME-ID-INVALID",
                                            value = """
                                                    {
                                                        "status": 400,
                                                        "code": "ACC-STORAGE-VOLUME-ID-INVALID",
                                                        "message": "볼륨 ID 형식이 유효하지 않습니다 (UUID)."
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
                                            name = "ACC-STORAGE-FORBIDDEN",
                                            value = """
                                                    {
                                                        "status": 403,
                                                        "code": "ACC-STORAGE-FORBIDDEN",
                                                        "message": "해당 프로젝트의 스토리지 리소스에 접근할 권한이 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "볼륨을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-VOLUME-NOT-FOUND",
                                            value = """
                                                    {
                                                        "status": 404,
                                                        "code": "ACC-STORAGE-VOLUME-NOT-FOUND",
                                                        "message": "요청한 볼륨 ID를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 - Cinder API 통신 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-CINDER-API-FAILURE",
                                            value = """
                                                    {
                                                        "status": 500,
                                                        "code": "ACC-STORAGE-CINDER-API-FAILURE",
                                                        "message": "OpenStack Cinder API 통신 중 오류가 발생하였습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping("/{volumeId}")
    ResponseEntity<VolumeResponse> getVolumeDetails(
            @Parameter(description = "조회할 볼륨 ID", required = true)
            @PathVariable String volumeId,
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId
    );

    @Operation(
            summary = "볼륨 삭제",
            description = "지정된 볼륨을 비동기로 삭제합니다.\n\n"
                    + "**삭제 가능 상태**\n"
                    + "- available, error, error_restoring, error_extending, error_managing\n\n"
                    + "**삭제 거부 조건**\n"
                    + "- 볼륨이 인스턴스에 연결됨(attached)\n"
                    + "- 마이그레이션 진행 중(migrating)\n"
                    + "- 스냅샷이 존재함\n"
                    + "- 그룹에 속함\n"
                    + "- transfer 대기 중(awaiting-transfer)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "볼륨 삭제 성공(No Content)",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "202",
                    description = "볼륨 삭제 요청 접수 (Accepted, 비동기)",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-VOLUME-ID-INVALID",
                                            value = """
                                                    {
                                                        "status": 400,
                                                        "code": "ACC-STORAGE-VOLUME-ID-INVALID",
                                                        "message": "볼륨 ID 형식이 유효하지 않습니다 (UUID)."
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
                                            name = "ACC-STORAGE-FORBIDDEN",
                                            value = """
                                                    {
                                                        "status": 403,
                                                        "code": "ACC-STORAGE-FORBIDDEN",
                                                        "message": "해당 프로젝트의 스토리지 리소스에 접근할 권한이 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "요청한 볼륨 ID를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-VOLUME-NOT-FOUND",
                                            value = """
                                                    {
                                                        "status": 404,
                                                        "code": "ACC-STORAGE-VOLUME-NOT-FOUND",
                                                        "message": "요청한 볼륨 ID를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌 - 삭제 불가능한 상태 (연결됨, 스냅샷 존재, 마이그레이션 중 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-VOLUME-CONFLICT",
                                            value = """
                                                    {
                                                        "status": 409,
                                                        "code": "ACC-STORAGE-VOLUME-CONFLICT",
                                                        "message": "볼륨이 사용 중이거나 삭제/생성이 불가능한 상태입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 - Cinder API 통신 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-CINDER-API-FAILURE",
                                            value = """
                                                    {
                                                        "status": 500,
                                                        "code": "ACC-STORAGE-CINDER-API-FAILURE",
                                                        "message": "OpenStack Cinder API 통신 중 오류가 발생하였습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping(params = "volumeId")
    ResponseEntity<Void> deleteVolume(
            @Parameter(description = "삭제할 볼륨 ID", required = true, example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @RequestParam String volumeId,
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId
    );

    @Operation(
            summary = "볼륨 생성",
            description = "새 볼륨을 비동기로 생성합니다.\n\n"
                    + "**필수 정보**\n"
                    + "- size: 볼륨 크기 (GiB 단위, 1 이상)\n\n"
                    + "**선택 정보**\n"
                    + "- name: 볼륨 이름 (1~128자, 영문/중문 시작, 영문/숫자/특수문자 가능)\n"
                    + "- volumeType: 볼륨 타입 (기본값: DEFAULT)\n"
                    + "- description: 볼륨 설명"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "볼륨 생성 요청 접수 (Accepted)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VolumeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 요청 파라미터 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-VOLUME-SIZE-INVALID",
                                            value = """
                                                    {
                                                        "status": 400,
                                                        "code": "ACC-STORAGE-VOLUME-SIZE-INVALID",
                                                        "message": "볼륨 크기는 1 이상의 정수여야 합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "ACC-STORAGE-VOLUME-NAME-INVALID",
                                            value = """
                                                    {
                                                        "status": 400,
                                                        "code": "ACC-STORAGE-VOLUME-NAME-INVALID",
                                                        "message": "볼륨 이름은 1~128자여야 하며, 영문/중문으로 시작하고 [0-9, a-z, A-Z, \\"-_()[].:^\\" ] 문자만 포함할 수 있습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "ACC-STORAGE-SNAPSHOT-INVALID-PARAM",
                                            value = """
                                                    {
                                                        "status": 400,
                                                        "code": "ACC-STORAGE-SNAPSHOT-INVALID-PARAM",
                                                        "message": "필수 요청 파라미터가 누락되었거나 유효하지 않습니다."
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
                                            name = "ACC-STORAGE-FORBIDDEN",
                                            value = """
                                                    {
                                                        "status": 403,
                                                        "code": "ACC-STORAGE-FORBIDDEN",
                                                        "message": "해당 프로젝트의 스토리지 리소스에 접근할 권한이 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌 - 볼륨 쿼터 초과 또는 볼륨 생성 불가능한 상태",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-VOLUME-QUOTA-EXCEEDED",
                                            value = """
                                                    {
                                                        "status": 409,
                                                        "code": "ACC-STORAGE-VOLUME-QUOTA-EXCEEDED",
                                                        "message": "볼륨 생성 쿼터를 초과했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "ACC-STORAGE-VOLUME-CONFLICT",
                                            value = """
                                                    {
                                                        "status": 409,
                                                        "code": "ACC-STORAGE-VOLUME-CONFLICT",
                                                        "message": "볼륨이 사용 중이거나 삭제/생성이 불가능한 상태입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 - Cinder API 통신 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "ACC-STORAGE-CINDER-API-FAILURE",
                                            value = """
                                                    {
                                                        "status": 500,
                                                        "code": "ACC-STORAGE-CINDER-API-FAILURE",
                                                        "message": "OpenStack Cinder API 통신 중 오류가 발생하였습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    ResponseEntity<VolumeResponse> createVolume(
            @Parameter(description = "생성할 볼륨 정보", required = true)
            @RequestBody VolumeRequest request,
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true)
            String projectId
    );
}
