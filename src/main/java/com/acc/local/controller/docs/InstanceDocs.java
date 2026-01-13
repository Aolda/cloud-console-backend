package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.instance.InstanceActionRequest;
import com.acc.local.dto.instance.InstanceCreateRequest;
import com.acc.local.dto.instance.InstanceQuotaResponse;
import com.acc.local.dto.instance.InstanceResponse;
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

@RequestMapping("/api/v1/instances")
@Tag(name = "Instance", description = "인스턴스 Server API")
@SecurityRequirement(name = "access-token")
public interface InstanceDocs {

    @Operation(
            summary = "인스턴스 목록 조회",
            description = "프로젝트에 속한 인스턴스(VM) 목록을 페이지네이션 방식으로 조회합니다.\n\n"
                    + "**페이지네이션 (마커 기반)**\n"
                    + "- marker: 이전 조회의 경계 ID (첫 조회 시 null)\n"
                    + "- direction: next(기본, 다음 페이지) | prev(이전 페이지)\n"
                    + "- limit: 페이지 크기 (기본 10, 전체 조회는 0)\n\n"
                    + "**동작 방식**\n"
                    + "- next: id > marker 기준 오름차순 조회\n"
                    + "- prev: id < marker 기준으로 조회 후 역순 정렬하여 반환\n\n"
                    + "**예시 쿼리**\n"
                    + "- 첫 페이지: GET /api/v1/instances?projectId=xxx&limit=10\n"
                    + "- 다음 페이지: GET /api/v1/instances?projectId=xxx&marker=lastId&direction=next&limit=10\n"
                    + "- 이전 페이지: GET /api/v1/instances?projectId=xxx&marker=firstId&direction=prev&limit=10"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인스턴스 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 파라미터 형식이 올바르지 않음",
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
                    description = "서버 오류 - OpenStack Nova API 호출 실패",
                    content = @Content()
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<InstanceResponse>> getInstances(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 고유 ID", required = true, example = "project-uuid-1234")
            String projectId,
            @Parameter(description = "페이지네이션 정보 (marker, direction, limit)")
            PageRequest page
    );

    @Operation(
            summary = "인스턴스 생성",
            description = "새로운 인스턴스(VM)를 생성합니다.\n\n"
                    + "**필수 정보**\n"
                    + "- 인스턴스 이름 (instanceName)\n"
                    + "  - 형식: Ubuntu 호스트명 규칙 (RFC 1123)\n"
                    + "  - 시작: 알파벳 또는 숫자\n"
                    + "  - 허용 문자: 알파벳, 숫자, 하이픈(-)\n"
                    + "  - 종료: 알파벳 또는 숫자 (하이픈으로 끝날 수 없음)\n"
                    + "  - 길이: 최대 63자\n"
                    + "  - 예시: web-server-01, db-master, app-1\n"
                    + "- 인증 방식 (keypairName 또는 password 중 택1)\n"
                    + "  - keypairName: OpenStack에 등록된 키페어 이름을 사용해야 합니다\n"
                    + "  - 새 키페어가 필요한 경우 먼저 키페어 생성 API를 사용하세요\n"
                    + "  - 존재하지 않는 키페어 이름 사용 시 400 Bad Request 반환\n"
                    + "- 이미지 ID (imageId)\n"
                    + "- 네트워크 연결 (networkIds 또는 interfaceIds 중 최소 1개 필수, 동시 사용 가능):\n"
                    + "  - networkIds: 네트워크 UUID (자동으로 포트 생성)\n"
                    + "  - interfaceIds: 기존 포트 UUID (이미 생성된 포트 사용)\n"
                    + "- 인스턴스 타입 (typeId)\n\n"
                    + "**선택 정보**\n"
                    + "- 보안 그룹 ID (securityGroupIds)\n"
                    + "- 디스크 크기 (diskSize, null/0이면 이미지 기본 크기 사용)\n\n"
                    + "**쿼터 제한**\n"
                    + "- 쿼터 초과 시 403 Forbidden 반환"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "인스턴스 생성 요청 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 필수 파라미터 누락 또는 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "인스턴스 이름 형식 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-INSTANCE-INVALID-NAME",
                                                      "message": "인스턴스 이름이 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "인증 방식 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-INSTANCE-AUTH-METHOD-REQUIRED",
                                                      "message": "인증 방식은 Keypair 또는 Password 중 하나만 선택해야 합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "네트워크 연결 누락",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-INSTANCE-NETWORK-REQUIRED",
                                                      "message": "네트워크 ID 또는 인터페이스 ID 중 최소 1개가 필요합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "필수 파라미터 누락",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-INSTANCE-INVALID-PARAMETER",
                                                      "message": "필수 파라미터가 누락되었거나 형식이 잘못되었습니다."
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
                    description = "권한 없음 - 프로젝트 접근 권한이 없거나 쿼터 한도 초과",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "컴퓨트 쿼터 초과",
                                            value = """
                                                    {
                                                      "status": 403,
                                                      "code": "ACC-INSTANCE-COMPUTE-QUOTA-EXCEEDED",
                                                      "message": "컴퓨트 쿼터(vCPU, RAM, 개수)가 초과되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음 - 키페어, 이미지, 네트워크, 보안그룹 등을 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack Nova API 호출 실패",
                    content = @Content()
            )
    })
    @PostMapping
    ResponseEntity<Object> createInstance(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 고유 ID", required = true, example = "project-uuid-1234")
            String projectId,
            @RequestBody
            @Parameter(description = "인스턴스 생성 요청 정보", required = true)
            InstanceCreateRequest request
    );

    @Operation(
            summary = "인스턴스 작업(Action) 수행",
            description = "지정된 인스턴스에 대해 작업을 수행합니다.\n\n"
                    + "**요청 방식**\n"
                    + "- Request Body (InstanceActionRequest)의 action 필드에 수행할 작업을 명시\n"
                    + "- 선택한 action에 따라 추가 필드가 요구될 수 있음\n\n"
                    + "**사용 가능한 Action 목록**\n"
                    + "- ADD_SECURITY_GROUP: 보안 그룹 추가\n"
                    + "- CHANGE_PASSWORD: 비밀번호 변경\n"
                    + "- CONFIRM_RESIZE: 크기 변경 확인\n"
                    + "- LOCK: 잠금\n"
                    + "- PAUSE: 일시 중지\n"
                    + "- REBOOT: 재부팅\n"
                    + "- REMOVE_SECURITY_GROUP: 보안 그룹 제거\n"
                    + "- RESIZE: 크기 변경\n"
                    + "- RESUME: 다시 시작\n"
                    + "- REVERT_RESIZE: 크기 변경 롤백\n"
                    + "- START: 시작\n"
                    + "- STOP: 정지\n"
                    + "- SUSPEND: 절전\n"
                    + "- UNLOCK: 잠금 해제\n"
                    + "- UNPAUSE: 일시 중지 해제\n"
                    + "- FORCE_DELETE: 강제 삭제\n"
//                    + "- REBUILD: 재구축\n"
//                    + "- RESCUE: 복구 모드\n"
//                    + "- UNRESCUE: 복구 모드 해제\n"
//                    + "- CREATE_BACKUP: 백업 생성\n"
//                    + "- CREATE_IMAGE: 이미지 생성\n"
//                    + "- RESTORE: 복원\n"
//                    + "- SHELVE: 보관\n"
//                    + "- SHELVE_OFFLOAD: 보관(오프로드)\n"
//                    + "- UNSHELVE: 보관 해제\n"
                    + "\n"
                    + "**참고**\n"
                    + "- 인스턴스의 현재 상태에 따라 수행 가능한 작업이 제한될 수 있음"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인스턴스 작업 요청 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 지원되지 않는 Action이거나 필수 파라미터 누락",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "유효하지 않은 Action",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-INSTANCE-INVALID-ACTION",
                                                      "message": "요청한 동작(action)을 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "필수 파라미터 누락",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-INSTANCE-INVALID-PARAMETER",
                                                      "message": "필수 파라미터가 누락되었거나 형식이 잘못되었습니다."
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
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음 - 지정한 인스턴스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "상태 오류 - 현재 인스턴스 상태에서는 해당 동작을 수행할 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack Nova API 호출 실패",
                    content = @Content()
            )
    })
    @PostMapping("/action")
    ResponseEntity<Object> controlInstance(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 고유 ID", required = true, example = "project-uuid-1234")
            String projectId,
            @RequestParam("instanceId")
            @Parameter(description = "인스턴스 고유 ID", required = true, example = "vm-uuid-1234-5678")
            String instanceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "인스턴스 작업 요청 정보 - action 필드에 따라 추가 필드가 필요할 수 있습니다",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "START - 인스턴스 시작",
                                            description = "SHUTOFF 상태의 인스턴스를 시작합니다",
                                            value = """
                                                    {
                                                        "action": "START"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "STOP - 인스턴스 정지",
                                            description = "ACTIVE 또는 ERROR 상태의 인스턴스를 정지합니다",
                                            value = """
                                                    {
                                                        "action": "STOP"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "REBOOT (SOFT) - 소프트 재부팅",
                                            description = "정상적인 종료 후 재시작 (ACTIVE 상태 필요)",
                                            value = """
                                                    {
                                                        "action": "REBOOT",
                                                        "type": "SOFT"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "REBOOT (HARD) - 하드 재부팅",
                                            description = "강제 종료 후 재시작 (전원 순환)",
                                            value = """
                                                    {
                                                        "action": "REBOOT",
                                                        "type": "HARD"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "PAUSE - 일시 중지",
                                            description = "인스턴스를 일시 중지합니다 (메모리 상태 유지)",
                                            value = """
                                                    {
                                                        "action": "PAUSE"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "UNPAUSE - 일시 중지 해제",
                                            description = "PAUSED 상태의 인스턴스를 재개합니다",
                                            value = """
                                                    {
                                                        "action": "UNPAUSE"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "SUSPEND - 절전",
                                            description = "인스턴스를 절전 모드로 전환합니다",
                                            value = """
                                                    {
                                                        "action": "SUSPEND"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "RESUME - 재개",
                                            description = "SUSPENDED 상태의 인스턴스를 재개합니다",
                                            value = """
                                                    {
                                                        "action": "RESUME"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "LOCK - 잠금",
                                            description = "인스턴스를 잠급니다 (일반 사용자의 작업 제한)",
                                            value = """
                                                    {
                                                        "action": "LOCK"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "LOCK (사유 포함)",
                                            description = "잠금 사유를 포함하여 인스턴스를 잠급니다",
                                            value = """
                                                    {
                                                        "action": "LOCK",
                                                        "lockedReason": "Maintenance in progress"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "UNLOCK - 잠금 해제",
                                            description = "잠긴 인스턴스의 잠금을 해제합니다",
                                            value = """
                                                    {
                                                        "action": "UNLOCK"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "ADD_SECURITY_GROUP - 보안 그룹 추가",
                                            description = "인스턴스에 보안 그룹을 추가합니다",
                                            value = """
                                                    {
                                                        "action": "ADD_SECURITY_GROUP",
                                                        "name": "web-security-group"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "REMOVE_SECURITY_GROUP - 보안 그룹 제거",
                                            description = "인스턴스에서 보안 그룹을 제거합니다",
                                            value = """
                                                    {
                                                        "action": "REMOVE_SECURITY_GROUP",
                                                        "name": "web-security-group"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "CHANGE_PASSWORD - 비밀번호 변경",
                                            description = "인스턴스의 관리자 비밀번호를 변경합니다",
                                            value = """
                                                    {
                                                        "action": "CHANGE_PASSWORD",
                                                        "adminPass": "NewSecurePassword123!"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "RESIZE - 크기 변경",
                                            description = "인스턴스의 Flavor를 변경합니다 (ACTIVE 또는 SHUTOFF 상태 필요)",
                                            value = """
                                                    {
                                                        "action": "RESIZE",
                                                        "flavorRef": "flavor-uuid-5678"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "CONFIRM_RESIZE - 크기 변경 확인",
                                            description = "보류 중인 크기 변경을 확인합니다 (VERIFY_RESIZE 상태 필요)",
                                            value = """
                                                    {
                                                        "action": "CONFIRM_RESIZE"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "REVERT_RESIZE - 크기 변경 롤백",
                                            description = "보류 중인 크기 변경을 취소합니다 (VERIFY_RESIZE 상태 필요)",
                                            value = """
                                                    {
                                                        "action": "REVERT_RESIZE"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "FORCE_DELETE - 강제 삭제",
                                            description = "지연된 정리 작업 전에 인스턴스를 강제로 삭제합니다",
                                            value = """
                                                    {
                                                        "action": "FORCE_DELETE"
                                                    }
                                                    """
                                    )
//                                    @ExampleObject(
//                                            name = "REBUILD (기본)",
//                                            description = "인스턴스를 새 이미지로 재구축합니다",
//                                            value = """
//                                                    {
//                                                        "action": "REBUILD",
//                                                        "imageRef": "image-uuid-5678"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "REBUILD (전체 옵션)",
//                                            description = "인스턴스를 재구축하면서 이름, 비밀번호, 메타데이터 등을 변경합니다",
//                                            value = """
//                                                    {
//                                                        "action": "REBUILD",
//                                                        "imageRef": "image-uuid-5678",
//                                                        "name": "rebuilt-server",
//                                                        "adminPass": "NewPassword123!",
//                                                        "metadata": {
//                                                            "environment": "production",
//                                                            "version": "2.0"
//                                                        },
//                                                        "description": "Rebuilt after security update",
//                                                        "keyName": "my-keypair"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "CREATE_IMAGE - 이미지 생성",
//                                            description = "인스턴스의 스냅샷 이미지를 생성합니다",
//                                            value = """
//                                                    {
//                                                        "action": "CREATE_IMAGE",
//                                                        "name": "my-server-snapshot-2026-01-03"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "CREATE_IMAGE (메타데이터 포함)",
//                                            description = "메타데이터를 포함하여 이미지를 생성합니다",
//                                            value = """
//                                                    {
//                                                        "action": "CREATE_IMAGE",
//                                                        "name": "my-server-snapshot-2026-01-03",
//                                                        "metadata": {
//                                                            "description": "Backup before major update",
//                                                            "created_by": "admin",
//                                                            "version": "1.5"
//                                                        }
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "CREATE_BACKUP - 백업 생성",
//                                            description = "인스턴스의 백업을 생성합니다 (볼륨 기반 인스턴스 미지원)",
//                                            value = """
//                                                    {
//                                                        "action": "CREATE_BACKUP",
//                                                        "name": "Daily Backup 2026-01-03",
//                                                        "backupType": "daily",
//                                                        "rotation": 7
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "CREATE_BACKUP (메타데이터 포함)",
//                                            description = "메타데이터를 포함하여 백업을 생성합니다",
//                                            value = """
//                                                    {
//                                                        "action": "CREATE_BACKUP",
//                                                        "name": "Weekly Backup 2026-01-03",
//                                                        "backupType": "weekly",
//                                                        "rotation": 4,
//                                                        "metadata": {
//                                                            "schedule": "every-sunday",
//                                                            "retention": "1-month"
//                                                        }
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "RESCUE - 복구 모드",
//                                            description = "인스턴스를 복구 모드로 전환합니다",
//                                            value = """
//                                                    {
//                                                        "action": "RESCUE"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "RESCUE (전체 옵션)",
//                                            description = "복구 이미지와 비밀번호를 지정하여 복구 모드로 전환합니다",
//                                            value = """
//                                                    {
//                                                        "action": "RESCUE",
//                                                        "adminPass": "RescuePassword123!",
//                                                        "rescueImageRef": "rescue-image-uuid-1234"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "UNRESCUE - 복구 모드 해제",
//                                            description = "RESCUE 상태의 인스턴스를 정상 모드로 되돌립니다",
//                                            value = """
//                                                    {
//                                                        "action": "UNRESCUE"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "SHELVE - 보관",
//                                            description = "인스턴스를 보관합니다 (이미지 생성 후 리소스 최소화)",
//                                            value = """
//                                                    {
//                                                        "action": "SHELVE"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "SHELVE_OFFLOAD - 보관 오프로드",
//                                            description = "SHELVED 상태의 인스턴스를 하이퍼바이저에서 제거합니다",
//                                            value = """
//                                                    {
//                                                        "action": "SHELVE_OFFLOAD"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "UNSHELVE - 보관 해제",
//                                            description = "보관된 인스턴스를 복원합니다",
//                                            value = """
//                                                    {
//                                                        "action": "UNSHELVE"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "UNSHELVE (가용 영역 지정)",
//                                            description = "특정 가용 영역에 인스턴스를 복원합니다",
//                                            value = """
//                                                    {
//                                                        "action": "UNSHELVE",
//                                                        "availabilityZone": "us-west"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "UNSHELVE (호스트 지정)",
//                                            description = "특정 호스트에 인스턴스를 복원합니다 (PROJECT_ADMIN 권한 필요)",
//                                            value = """
//                                                    {
//                                                        "action": "UNSHELVE",
//                                                        "host": "compute-node-01.example.com"
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "UNSHELVE (가용 영역 고정 해제)",
//                                            description = "가용 영역 고정을 해제하고 인스턴스를 복원합니다",
//                                            value = """
//                                                    {
//                                                        "action": "UNSHELVE",
//                                                        "availabilityZone": null
//                                                    }
//                                                    """
//                                    ),
//                                    @ExampleObject(
//                                            name = "RESTORE - 복원",
//                                            description = "SOFT_DELETED 상태의 인스턴스를 복원합니다",
//                                            value = """
//                                                    {
//                                                        "action": "RESTORE"
//                                                    }
//                                                    """
//                                    )
                            }
                    )
            )
            @RequestBody InstanceActionRequest request
    );

    @Operation(
            summary = "컴퓨트 쿼터 조회",
            description = "프로젝트의 컴퓨트 관련 리소스 쿼터를 조회합니다.\n\n"
                    + "**조회 정보**\n"
                    + "- vCPU 사용량 및 한도\n"
                    + "- RAM 사용량 및 한도 (단위: MB)\n"
                    + "- 인스턴스 수 사용량 및 한도\n"
                    + "- 키페어 사용량 및 한도\n\n"
                    + "**참고**\n"
                    + "- 쿼터 정보는 인스턴스 생성 전 가용 리소스 확인에 활용"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿼터 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 파라미터 형식이 올바르지 않음",
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
                    description = "서버 오류 - OpenStack Nova API 호출 실패",
                    content = @Content()
            )
    })
    @GetMapping("/quota")
    ResponseEntity<InstanceQuotaResponse> getQuota(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "프로젝트 고유 ID", required = true, example = "project-uuid-1234")
            String projectId
    );
}
