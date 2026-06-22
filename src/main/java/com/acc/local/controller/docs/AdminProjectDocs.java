package com.acc.local.controller.docs;

import java.util.List;

import com.acc.global.exception.project.ProjectErrorCode;
import com.acc.local.dto.project.*;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.ProjectRoleResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/v1/admin/projects")
@Tag(name = "Admin Project", description = "관리자용 프로젝트 API")
@SecurityRequirement(name = "access-token")
public interface AdminProjectDocs {

	@Operation(
		summary = "[관리자] 프로젝트 목록 조회",
		description = "전체 프로젝트 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "프로젝트 목록 조회 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = PageResponse.class),
				examples = {
					@ExampleObject(
						name = "관리자 프로젝트 목록 조회",
						value = """
							{
							  "contents": [
							    {
							      "projectId": "0cc61cc8ed964714a06a42afa92c1dc6",
							      "projectName": "admin",
							      "projectType": "PROJECT_REQUEST_TYPE/ETC",
							      "createdBy": null,
							      "createdAt": "1900-01-01T00:00",
							      "status": "APPROVED",
							      "quota": {
							        "instance": { "available": 10, "used": 2 },
							        "core": { "available": 20, "used": 2 },
							        "ram": { "available": 51200, "used": 1024 },
							        "volume": {
							          "count": { "available": 10, "used": 2 },
							          "size": { "available": 1000, "used": 30 }
							        }
							      },
							      "participants": [],
							      "rejectReason": null
							    }
							  ],
							  "first": true,
							  "last": false,
							  "size": 1,
							  "nextMarker": "0cc61cc8ed964714a06a42afa92c1dc6",
							  "prevMarker": null
							}
							"""
					)
				}
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 요청 파라미터 오류", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@GetMapping
	ResponseEntity<PageResponse<ProjectResponse>> getProjects(
		@Parameter(hidden = true) Authentication authentication,
		@Parameter(description = "검색어; 프로젝트 제목만 검색가능", required = false) @RequestParam(required = false) String keyword,
		@ParameterObject @ModelAttribute PageRequest page
	);

    @Operation(
            summary = "[관리자] 프로젝트 생성",
            description = "(관리자 전용) 실제 프로젝트를 생성합니다.\n\n"
                    + "- 이 엔드포인트는 승인과 별개로, Keystone 프로젝트 생성/쿼터 적용/오너 역할 부여/기본 네트워크 생성까지 수행합니다.\n"
                    + "- projectOwnerId는 Keystone 사용자 ID이며, 생성된 프로젝트에 PROJECT_ADMIN 역할이 부여됩니다.\n"
                    + "- 기본 네트워크는 오너의 프로젝트 스코프 토큰으로 자동 생성됩니다."
    )
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "프로젝트 생성 성공", content = @Content()),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 입력값 오류", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
    @PostMapping
    ResponseEntity<CreateProjectResponse> createProject(
        @Parameter(hidden = true) Authentication authentication,
        @RequestBody(required = true) CreateProjectRequest request
    );

	// 3. [관리자] 프로젝트 권한목록 조회
	@Operation(
		summary = "[관리자] 프로젝트 권한목록 조회",
		description = "프로젝트에서 등록 가능한 권한 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "권한 목록 조회 성공", content = @Content()),
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@GetMapping("/roles")
	ResponseEntity<List<ProjectRoleResponse>> getProjectRoles(
		@Parameter(hidden = true) Authentication authentication
	);

	// 4. [관리자] 신규 프로젝트 생성요청 목록 조회
	@Operation(
		summary = "[관리자] 신규 프로젝트 생성요청 목록 조회",
		description = "모든 프로젝트 생성요청 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "생성요청 목록 조회 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = PageResponse.class),
				examples = {
					@ExampleObject(
						name = "관리자 프로젝트 생성요청 목록 조회",
						value = """
							{
							  "contents": [
							    {
							      "projectRequestId": "ad1af0b6-0ac7-4005-95b9-9b0a325f00a9",
							      "projectName": "캡스톤 프로젝트 요청",
							      "projectType": "PROJECT_REQUEST_TYPE/CAPSTONE_DESIGN",
							      "createdBy": {
							        "userId": "f52f7447cba1476da8fa281bf6fff220",
							        "userName": "Waccounttest_1"
							      },
							      "createdAt": "2025-11-24T02:04:36.473286",
							      "status": "PENDING",
							      "quota": {
							        "instance": { "available": 10, "used": 0 },
							        "core": { "available": 8, "used": 0 },
							        "ram": { "available": 32768, "used": 0 },
							        "volume": {
							          "count": { "available": 10, "used": 0 },
							          "size": { "available": 1024, "used": 0 }
							        }
							      }
							    }
							  ],
							  "first": true,
							  "last": false,
							  "size": 1,
							  "nextMarker": "ad1af0b6-0ac7-4005-95b9-9b0a325f00a9",
							  "prevMarker": null
							}
							"""
					)
				}
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@GetMapping("/request")
	ResponseEntity<PageResponse<ProjectRequestResponse>> getProjectRequests(
		@Parameter(hidden = true) Authentication authentication,
		@Parameter(description = "검색 키워드; 현재는 프로젝트 제목만 지원") @RequestParam(required = false) String keyword,
		@ParameterObject @ModelAttribute PageRequest pageable
	);

	// 5. [관리자] 신규 프로젝트 생성요청 승인/거절
    @Operation(
            summary = "[관리자] 신규 프로젝트 생성요청 승인/거절",
            description = "프로젝트 생성요청의 상태를 승인(APPROVED)/거절(REJECTED)로 변경하고, '승인'인 경우 요청정보를 바탕으로 실제 프로젝트를 생성합니다. \n\n" +
					"- \"requested\": 결정적용을 요청한 프로젝트요청 수\n" +
					"- \"acknowledged\": 실제로 존재하는 프로젝트요청임이 확인된 프로젝트요청 수\n" +
					"- \"applied\": 실제로 요청한 결정이 적용된 프로젝트요청 수\n" +
					"- \"data\": 각 요청건 별 상세처리정보"
    )
	@ApiResponses({
			// ----- 성공 응답 -----
			@ApiResponse(
					responseCode = "200",
					description = "프로젝트 요청 결정 적용 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = DecideProjectRequestResponse.class),
							examples = {
									@ExampleObject(
											name = "Success Response Example",
											summary = "[결정 적용 성공 예시]",
											value = """
												{
												  "requested": 5,
												  "acknowledged": 3,
												  "applied": 1,
												  "data": {
													"20895bda-0662-4da9-9b27-655934adc452": {
													  "isApplied": false,
													  "projectId": null,
													  "reason": "이미 승인/반려여부가 결정된 프로젝트 요청입니다."
													},
													"9a1f3d25-ed00-4f5b-a56c-8bf37c286b50": {
													  "isApplied": true,
													  "projectId": "d53b5904d091456a9ec67ec696079955",
													  "reason": null
													},
													"e820939b-4966-4799-b3c4-4d2a1130d85e": {
													  "isApplied": false,
													  "projectId": null,
													  "reason": "이미 승인/반려여부가 결정된 프로젝트 요청입니다."
													}
												  }
												}
                                        	"""
									)
							}
					)
			),
			@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content()),
			@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
			@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
			@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
    @PostMapping("/request")
    ResponseEntity<DecideProjectRequestResponse> decideProjectRequest(
        @Parameter(hidden = true) Authentication authentication,
        @RequestBody(required = true) DecideProjectRequestRequest request
    );
}
