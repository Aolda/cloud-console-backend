package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairListResponse;
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

@RequestMapping("/api/v1/keypairs")
@Tag(name = "Keypair", description = "키페어 API")
@SecurityRequirement(name = "access-token")
public interface KeypairDocs {

    @Operation(
            summary = "키페어 목록 조회",
            description = "프로젝트에 속한 키페어 목록을 조회합니다.\n\n"
                    + "**쿼리 파라미터**\n"
                    + "- projectId: 프로젝트 ID (필수)\n"
                    + "- marker: 페이지네이션 경계 ID (첫 조회 시 null)\n"
                    + "- direction: next(기본, 다음 페이지) 또는 prev(이전 페이지)\n"
                    + "- limit: 페이지 크기 (기본 10, 전체 조회는 0)\n\n"
                    + "**페이지네이션**\n"
                    + "- Marker 기반 페이지네이션 사용\n"
                    + "- next: id > marker 오름차순\n"
                    + "- prev: id < marker 내림차순으로 조회 후 뒤집어 반환\n\n"
                    + "**예시 쿼리**\n"
                    + "- 첫 페이지: GET /api/v1/keypairs?projectId=project-uuid-1234&limit=10\n"
                    + "- 다음 페이지: GET /api/v1/keypairs?projectId=project-uuid-1234&marker=keypair-id-10&direction=next&limit=10\n"
                    + "- 이전 페이지: GET /api/v1/keypairs?projectId=project-uuid-1234&marker=keypair-id-10&direction=prev&limit=10"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "키페어 목록 조회 성공"
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
                                                      "code": "ACC-KEYPAIR-DB-PROJECT-NOT-FOUND",
                                                      "message": "프로젝트를 찾을 수 없습니다. (DB)"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - DB 조회 오류",
                    content = @Content()
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<KeypairListResponse>> getKeypairs(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "페이지 정보 (Marker 기반)", required = false)
            PageRequest page,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true, example = "project-uuid-1234")
            String projectId);


    @Operation(
            summary = "키페어 생성",
            description = "새로운 키페어를 생성합니다.\n\n"
                    + "**필수 정보**\n"
                    + "- 키페어 이름 (keypairName)\n"
                    + "- 프로젝트 ID (projectId)\n\n"
                    + "**동작 방식**\n"
                    + "- OpenStack에서 키페어를 생성합니다\n"
                    + "- 생성된 공개 키와 개인 키를 반환합니다\n"
                    + "- 개인 키는 생성 시에만 반환되므로 반드시 저장해야 합니다\n\n"
                    + "**주의 사항**\n"
                    + "- 키페어 이름은 프로젝트 내에서 고유해야 합니다\n"
                    + "- 개인 키는 재발급이 불가능합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "키페어 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 유효하지 않은 키페어 이름 형식",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "키페어 이름 형식 오류",
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "code": "ACC-KEYPAIR-INVALID-NAME",
                                                      "message": "키페어 이름이 유효하지 않습니다."
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
                    description = "리소스 없음 - 프로젝트를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "프로젝트 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-KEYPAIR-DB-PROJECT-NOT-FOUND",
                                                      "message": "프로젝트를 찾을 수 없습니다. (DB)"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack 키페어 생성 실패 또는 DB 저장 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "DB 저장 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-KEYPAIR-DB-SAVE-FAILED",
                                                      "message": "키페어 정보를 DB에 저장하는 데 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    ResponseEntity<CreateKeypairResponse> createKeypair(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestBody
            @Parameter(description = "키페어 생성 요청 정보", required = true)
            CreateKeypairRequest request,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true, example = "project-uuid-1234")
            String projectId);


    @Operation(
            summary = "키페어 삭제",
            description = "지정한 키페어를 삭제합니다.\n\n"
                    + "**필수 정보**\n"
                    + "- 키페어 ID (keypairId): 키페어의 핑거프린트\n"
                    + "- 프로젝트 ID (projectId)\n\n"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "키페어 삭제 성공",
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
                    description = "리소스 없음 - 지정한 키페어를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "키페어 없음",
                                            value = """
                                                    {
                                                      "status": 404,
                                                      "code": "ACC-KEYPAIR-DB-NOT-FOUND",
                                                      "message": "키페어를 찾을 수 없습니다. (DB)"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - OpenStack 삭제 실패 또는 DB 삭제 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "DB 삭제 실패",
                                            value = """
                                                    {
                                                      "status": 500,
                                                      "code": "ACC-KEYPAIR-DB-DELETION-FAILED",
                                                      "message": "키페어 정보를 DB에서 삭제하는 데 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping
    ResponseEntity<Object> deleteKeypair(
            @Parameter(hidden = true)
            Authentication authentication,
            @RequestParam
            @Parameter(description = "삭제할 키페어의 ID (핑거프린트)", required = true, example = "aa:bb:cc:dd:ee:ff:11:22:33:44:55:66:77:88:99:00")
            String keypairId,
            @RequestParam
            @Parameter(description = "프로젝트 ID", required = true, example = "project-uuid-1234")
            String projectId);
}
