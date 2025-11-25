package com.acc.local.controller.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.project.CreateProjectRequestResponse;
import com.acc.local.dto.project.GetProjectResponse;
import com.acc.local.dto.project.InvitableUser;
import com.acc.local.dto.project.InviteProjectRequest;
import com.acc.local.dto.project.CreateProjectRequestRequest;
import com.acc.local.dto.project.KickOutProjectRequest;
import com.acc.local.dto.project.ProjectParticipantDto;
import com.acc.local.dto.project.ProjectRequestResponse;
import com.acc.local.dto.project.ProjectResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/v1/projects")
@Tag(name = "Project", description = "회원용 프로젝트 API")
@SecurityRequirement(name = "access-token")
public interface ProjectDocs {

	@Operation(
		summary = "프로젝트 목록 조회",
		description = "로그인된 사용자가 접근권한을 가지고 있는 프로젝트들에 대한 목록을 조회합니다. <br>"
			+ "(프로젝트 요청은 본 응답에 포함되지 않습니다) <br>"
			+ "- keyword는 현재 프로젝트 제목만 입력 가능하며, 구현문제로 현재는 정확히 일치하는 경우에 대해서면 응답됩니다"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "프로젝트 목록 조회 성공",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(
						name = "프로젝트 목록 조회",
						value = "[\n"
							+ "  {\n"
							+ "    \"projectId\": \"0cc61cc8ed964714a06a42afa92c1dc6\",\n"
							+ "    \"projectName\": \"admin\",\n"
							+ "    \"projectType\": null,\n"
							+ "    \"createdBy\": null,\n"
							+ "    \"createdAt\": null,\n"
							+ "    \"status\": \"APPROVED\",\n"
							+ "    \"projectBrief\": null,\n"
							+ "    \"participants\": []\n"
							+ "  },\n"
							+ "  {\n"
							+ "    \"projectId\": \"1a8741e7d4bd4ddb8046f93a531215a1\",\n"
							+ "    \"projectName\": \"ZsefsefesfesZABVE\",\n"
							+ "    \"projectType\": null,\n"
							+ "    \"createdBy\": {\n"
							+ "      \"userId\": \"de15e36af072460da2e39a74be3595b6\",\n"
							+ "      \"userName\": \"Acc_test_Admin1\"\n"
							+ "    },\n"
							+ "    \"createdAt\": \"2025-11-25T00:25:13.549017\",\n"
							+ "    \"status\": \"APPROVED\",\n"
							+ "    \"projectBrief\": null,\n"
							+ "    \"participants\": []\n"
							+ "  }\n"
							+ "]"
					),
					@ExampleObject(
						name = "프로젝트 상세정보 조회",
						value = "{\n"
							+ "  \"projectId\": \"a2b65bb6cbe14cf89b7b120480c92acf\",\n"
							+ "  \"projectName\": \"AVC1awfwef24234\",\n"
							+ "  \"description\": \"AVC124234\",\n"
							+ "  \"isActive\": true,\n"
							+ "  \"projectType\": null,\n"
							+ "  \"ownerKeystoneId\": \"de15e36af072460da2e39a74be3595b6\",\n"
							+ "  \"createdAt\": \"2025-11-25T02:06:04.158384\",\n"
							+ "  \"status\": \"APPROVED\",\n"
							+ "  \"quota\": null,\n"
							+ "  \"participants\": [\n"
							+ "    {\n"
							+ "      \"userId\": \"de15e36af072460da2e39a74be3595b6\",\n"
							+ "      \"userName\": \"${SUPER_ADMIN_USER_NAME}\",\n"
							+ "      \"userEmail\": null,\n"
							+ "      \"userPhoneNumber\": \"010-0000-0000\",\n"
							+ "      \"role\": \"PROJECT_ADMIN\"\n"
							+ "    }\n"
							+ "  ]\n"
							+ "}"
					)
				},
				schema = @Schema(oneOf = {ProjectResponse.class, List.class})
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 요청 파라미터 오류", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@GetMapping(params = "!projectId")
	ResponseEntity<List<ProjectResponse>> getProjects(
		@Parameter(hidden = true) Authentication authentication,
		@Parameter(description = "검색어; 프로젝트 제목만 검색가능", required = false) @RequestParam(required = false) String keyword
	);

	@Operation(
		summary = "프로젝트 상세정보 조회",
		description = "특정 프로젝트의 세부정보를 조회합니다"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "프로젝트 상세정보 조회 성공",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(
						name = "프로젝트 상세정보 조회",
						value = "{\n"
							+ "  \"projectId\": \"a2b65bb6cbe14cf89b7b120480c92acf\",\n"
							+ "  \"projectName\": \"AVC1awfwef24234\",\n"
							+ "  \"description\": \"AVC124234\",\n"
							+ "  \"isActive\": true,\n"
							+ "  \"projectType\": null,\n"
							+ "  \"ownerKeystoneId\": \"de15e36af072460da2e39a74be3595b6\",\n"
							+ "  \"createdAt\": \"2025-11-25T02:06:04.158384\",\n"
							+ "  \"status\": \"APPROVED\",\n"
							+ "  \"quota\": null,\n"
							+ "  \"participants\": [\n"
							+ "    {\n"
							+ "      \"userId\": \"de15e36af072460da2e39a74be3595b6\",\n"
							+ "      \"userName\": \"${SUPER_ADMIN_USER_NAME}\",\n"
							+ "      \"userEmail\": null,\n"
							+ "      \"userPhoneNumber\": \"010-0000-0000\",\n"
							+ "      \"role\": \"PROJECT_ADMIN\"\n"
							+ "    }\n"
							+ "  ]\n"
							+ "}"
					),
					@ExampleObject(
						name = "프로젝트 목록 조회",
						value = "[\n"
							+ "  {\n"
							+ "    \"projectId\": \"0cc61cc8ed964714a06a42afa92c1dc6\",\n"
							+ "    \"projectName\": \"admin\",\n"
							+ "    \"projectType\": null,\n"
							+ "    \"createdBy\": null,\n"
							+ "    \"createdAt\": null,\n"
							+ "    \"status\": \"APPROVED\",\n"
							+ "    \"projectBrief\": null,\n"
							+ "    \"participants\": []\n"
							+ "  },\n"
							+ "  {\n"
							+ "    \"projectId\": \"1a8741e7d4bd4ddb8046f93a531215a1\",\n"
							+ "    \"projectName\": \"ZsefsefesfesZABVE\",\n"
							+ "    \"projectType\": null,\n"
							+ "    \"createdBy\": {\n"
							+ "      \"userId\": \"de15e36af072460da2e39a74be3595b6\",\n"
							+ "      \"userName\": \"Acc_test_Admin1\"\n"
							+ "    },\n"
							+ "    \"createdAt\": \"2025-11-25T00:25:13.549017\",\n"
							+ "    \"status\": \"APPROVED\",\n"
							+ "    \"projectBrief\": null,\n"
							+ "    \"participants\": []\n"
							+ "  }\n"
							+ "]"
					)
				},
				schema = @Schema(oneOf = {ProjectResponse.class, List.class})
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 요청 파라미터 오류", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@GetMapping(params = "projectId")
	ResponseEntity<GetProjectResponse> getProjectSpec(
		@Parameter(hidden = true) Authentication authentication,
		@Parameter(description = "상세조회하고자 하는 프로젝트 ID", required = false) @RequestParam(required = false) String projectId
	);

	@Operation(
		summary = "신규 프로젝트 생성요청 목록 조회 ",
		description = "로그인된 사용자가 작성한 프로젝트 생성요청 목록을 조회합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "프로젝트 생성요청 목록 조회 성공",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(
						name = "프로젝트 생성요청 목록 조회",
						value = "{\n"
							+ "  \"contents\": [\n"
							+ "    {\n"
							+ "      \"projectRequestId\": \"ad1af0b6-0ac7-4005-95b9-9b0a325f00a9\",\n"
							+ "      \"projectName\": \"abcabcasefes\",\n"
							+ "      \"projectType\": \"PROJECT_REQUEST_TYPE/CAPSTONE_DESIGN\",\n"
							+ "      \"createdBy\": {\n"
							+ "        \"userId\": \"f52f7447cba1476da8fa281bf6fff220\",\n"
							+ "        \"userName\": \"Waccounttest_1\"\n"
							+ "      },\n"
							+ "      \"createdAt\": \"2025-11-24T02:04:36.473286\",\n"
							+ "      \"status\": \"APPROVED\",\n"
							+ "      \"projectBrief\": {\n"
							+ "        \"vCpu\": 8,\n"
							+ "        \"vRam\": 32,\n"
							+ "        \"instance\": 10,\n"
							+ "        \"storage\": 1000\n"
							+ "      }\n"
							+ "    },\n"
							+ "    {\n"
							+ "      \"projectRequestId\": \"bb02de58-1376-4e6d-b676-6b088f4c41f5\",\n"
							+ "      \"projectName\": \"abcabcasefes\",\n"
							+ "      \"projectType\": \"PROJECT_REQUEST_TYPE/CAPSTONE_DESIGN\",\n"
							+ "      \"createdBy\": {\n"
							+ "        \"userId\": \"f52f7447cba1476da8fa281bf6fff220\",\n"
							+ "        \"userName\": \"Waccounttest_1\"\n"
							+ "      },\n"
							+ "      \"createdAt\": \"2025-11-24T02:00:31.440323\",\n"
							+ "      \"status\": \"PENDING\",\n"
							+ "      \"projectBrief\": {\n"
							+ "        \"vCpu\": 8,\n"
							+ "        \"vRam\": 32,\n"
							+ "        \"instance\": 10,\n"
							+ "        \"storage\": 1000\n"
							+ "      }\n"
							+ "    }\n"
							+ "  ],\n"
							+ "  \"first\": true,\n"
							+ "  \"last\": false,\n"
							+ "  \"size\": 2,\n"
							+ "  \"nextMarker\": \"MA==\"\n"
							+ "}"
					)
				},
				schema = @Schema(oneOf = {ProjectRequestResponse.class, PageResponse.class})
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 요청 파라미터 오류", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@GetMapping("/request")
	ResponseEntity<PageResponse<ProjectRequestResponse>> getProjectRequests(
		@Parameter(hidden = true) Authentication authentication,
		@Parameter(description = "검색 키워드; 현재는 프로젝트 제목만 지원") @RequestParam(required = false) String keyword,
		@Parameter(description = "페이지 정보", required = false) @RequestParam(required = false) PageRequest pageable
	);

	@Operation(
		summary = "신규 프로젝트 생성요청 등록",
		description = "신규 프로젝트 생성을 요청합니다"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201",
			description = "프로젝트 생성요청 등록 성공",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(
						name = "프로젝트 생성요청 등록 성공",
						value = "{ \n"
							+ "  \"projectRequestId\": \"f01213b2-6900-4086-b3fa-a1bbdd36765c\",\n"
							+ "  \"projectName\": \"가나다라마바사\",\n"
							+ "  \"projectType\": \"PROJECT_REQUEST_TYPE/MAJOR_LECTURE\",\n"
							+ "  \"createdAt\": \"2025-11-25T02:14:58.849064\",\n"
							+ "  \"status\": \"PENDING\",\n"
							+ "  \"participants\": null\n"
							+ "}"
					)
				},
				schema = @Schema(oneOf = {CreateProjectRequestResponse.class})
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 요청 파라미터 오류", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@PostMapping("/request")
	ResponseEntity<CreateProjectRequestResponse> createProjectRequest(
		@Parameter(hidden = true) Authentication authentication,
		@RequestBody CreateProjectRequestRequest createProjectRequestRequest
	);

	@Operation(
		summary = "프로젝트 참여자 초대",
		description = "프로젝트에 참여하는 사용자를 초대합니다"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "프로젝트 참여자 초대 성공",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(
						name = "프로젝트 참여자 초대 성공",
						value = "[\n"
							+ "  {\n"
							+ "    \"userId\": \"f01213b2-6900-4086-b3fa-a1bbdd36765c,\"\n"
							+ "    \"userName\": \"가나다라마바사,\"\n"
							+ "    \"userEmail\": \"sefesf@ajou.ac.kr, // nullable\"\n"
							+ "    \"userPhoneNumber\": \"010-1234-1234,\"\n"
							+ "    \"role\": \"PROJECT_ROLE/PROJECT_ADMIN\"\n"
							+ "  },\n"
							+ "  {\n"
							+ "    \"userId\": \"f01213b2-6900-4086-b3fa-a1bbdd36765c,\"\n"
							+ "    \"userName\": \"가나다라마바사,\"\n"
							+ "    \"userEmail\": \"sefesf@ajou.ac.kr, // nullable\"\n"
							+ "    \"userPhoneNumber\": \"010-1234-1234,\"\n"
							+ "    \"role\": \"PROJECT_ROLE/PROJECT_ADMIN\"\n"
							+ "  },\n"
							+ "]"
					)
				},
				schema = @Schema(oneOf = {List.class})
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 요청 파라미터 오류", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@PostMapping("/participants")
	ResponseEntity<List<ProjectParticipantDto>> inviteProjectParticipants(
		@Parameter(hidden = true) Authentication authentication,
		@RequestBody InviteProjectRequest inviteProjectRequest
	);

	@Operation(
		summary = "프로젝트 참여자 삭제",
		description = "프로젝트에 참여하는 사용자를 삭제합니다 <br>"
			+ "프로젝트 관리자는 삭제할 수 없습니다"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "204",
			description = "프로젝트 참여자 삭제 성공",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(
						name = "프로젝트 참여자 삭제 성공",
						value = "[ \n"
							+ "  {\n"
							+ "    \"userId\": \"f01213b2-6900-4086-b3fa-a1bbdd36765c\",\n"
							+ "    \"userName\": \"가나다라마바사\",\n"
							+ "    \"userEmail\": \"sefesf@ajou.ac.kr\", // nullable\n"
							+ "    \"userPhoneNumber\": \"010-1234-1234\",\n"
							+ "    \"role\": \"PROJECT_ROLE/PROJECT_ADMIN\"\n"
							+ "  }\n"
							+ "]"
					)
				},
				schema = @Schema(oneOf = {List.class})
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 요청 파라미터 오류", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@DeleteMapping("/participants")
	ResponseEntity<List<ProjectParticipantDto>> kickOutProjectParticipants(
		@Parameter(hidden = true) Authentication authentication,
		@RequestBody KickOutProjectRequest inviteProjectRequest
	);

	@Operation(
		summary = "초대가능한 회원목록 검색",
		description = "이메일이나 이름을 사용하여 초대가능한 회원정보를 조회합니다"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "프로젝트 초대가능 회원목록 조회 성공",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(
						name = "프로젝트 초대가능 회원목록",
						value = "[\n"
							+ "  {\n"
							+ "    \"userId\": \"f01213b2-6900-4086-b3fa-a1bbdd36765c\",\n"
							+ "    \"userName\": \"가나다라마바사\",\n"
							+ "    \"userEmail\": \"sefesf@ajou.ac.kr\"\n"
							+ "  },\n"
							+ "  {\n"
							+ "    \"userId\": \"f01213b2-6900-4086-b3fa-a1bbdd36765c\",\n"
							+ "    \"userName\": \"가나다라마바사\",\n"
							+ "    \"userEmail\": \"sefesf@ajou.ac.kr\"\n"
							+ "  }\n"
							+ "]"
					)
				},
				schema = @Schema(oneOf = {List.class})
			)
		),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 요청 파라미터 오류", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@GetMapping("/participants/available")
	ResponseEntity<List<InvitableUser>> getInvitableParticipants(
		@Parameter(hidden = true) Authentication authentication,
		@Parameter(description = "검색하고자 하는 사용자 이름 혹은 이메일") @RequestParam(required = false) String keyword
	);

}
