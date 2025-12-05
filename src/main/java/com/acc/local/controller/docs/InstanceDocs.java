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
            description = "프로젝트에 속한 인스턴스(VM) 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인스턴스 목록 조회 성공"
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
                    description = "서버 오류 - Nova 서버 정보를 가져오는데 실패했습니다.",
                    content = @Content()
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<InstanceResponse>> getInstances(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page
    );

    @Operation(
            summary = "인스턴스 생성",
            description = "새로운 인스턴스(VM)를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "인스턴스 생성 요청 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - (이름, 인증, 이미지, 네트워크, 타입, 디스크 크기 등 파라미터 오류)",
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
                    description = "리소스 없음 - (키페어, 이미지, 네트워크, 보안그룹 등을 찾을 수 없음)",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "쿼터 초과 - (컴퓨트 또는 볼륨 쿼터 초과)",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - Nova 서버 생성에 실패했습니다.",
                    content = @Content()
            )
    })
    @PostMapping
    ResponseEntity<Object> createInstance(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestBody
            @Parameter(description = "인스턴스 생성 요청 정보", required = true)
            InstanceCreateRequest request
    );

    @Operation(
            summary = "쿼터 조회",
            description = "컴퓨트 관련 리소스 쿼터를 조회합니다"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿼터조회 성공"
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
                    description = "서버 오류 - Nova 서버 생성에 실패했습니다.",
                    content = @Content()
            )
    })
    @GetMapping("/quota")
    ResponseEntity<InstanceQuotaResponse> getQuota(
            @Parameter(hidden = true)
            Authentication authentication
    );

    @Operation(
            summary = "인스턴스 작업(Action) 수행",
            description = """
                                        - 지정된 인스턴스에 대해 작업을 수행합니다.
                                        - `InstanceActionRequest`의 `action` 필드에 수행할 작업을 명시하며,
                                        - 선택한 `action`에 따라 `InstanceActionRequest`의 다른 필드들이 요구될 수 있습니다.
                    
                                        ----- 사용 가능한 Action 목록  -----
                                        * ADD_SECURITY_GROUP: 보안 그룹 추가
                                        * CHANGE_PASSWORD: 비밀번호 변경
                                        * CONFIRM_RESIZE: 크기 변경 확인
                                        * CREATE_BACKUP: 백업 생성
                                        * CREATE_IMAGE: 이미지 생성
                                        * LOCK: 잠금
                                        * PAUSE: 일시 중지
                                        * REBOOT: 재부팅
                                        * REBUILD: 재구축
                                        * REMOVE_SECURITY_GROUP: 보안 그룹 제거
                                        * RESCUE: 복구 모드
                                        * RESIZE: 크기 변경
                                        * RESUME: 다시 시작
                                        * REVERT_RESIZE: 크기 변경 롤백
                                        * START: 시작
                                        * STOP: 정지
                                        * SUSPEND: 절전
                                        * UNLOCK: 잠금 해제
                                        * UNPAUSE: 일시 중지 해제
                                        * UNRESCUE: 복구 모드 해제
                                        * FORCE_DELETE: 강제 삭제
                                        * RESTORE: 복원
                                        * SHELVE: 보관
                                        * SHELVE_OFFLOAD: 보관(오프로드)
                                        * UNSHELVE: 보관 해제
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인스턴스 작업 요청 성공",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - (지원되지 않는 Action이거나 Action에 필요한 파라미터 누락)",
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
                    description = "인스턴스 없음 - 지정한 인스턴스를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "상태 오류 - 현재 인스턴스 상태에서는 해당 동작을 수행할 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - Nova 서버 Action 수행에 실패했습니다.",
                    content = @Content()
            )
    })
    @PostMapping("/action")
    ResponseEntity<Object> controlInstance(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam("instanceId")
            @Parameter(description = "인스턴스 고유 ID", required = true, example = "vm-uuid-1234-5678")
            String instanceId,
            @RequestBody
            @Parameter(description = "인스턴스 작업 요청 정보", required = true)
            InstanceActionRequest request
    );
}
