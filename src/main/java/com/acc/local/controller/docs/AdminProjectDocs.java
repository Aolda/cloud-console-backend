package com.acc.local.controller.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.project.CreateProjectRequest;
import com.acc.local.dto.project.CreateProjectResponse;
import com.acc.local.dto.project.DecideProjectRequestRequest;
import com.acc.local.dto.auth.ProjectRoleResponse;
import com.acc.local.dto.project.ProjectRequestResponse;
import com.acc.local.dto.project.ProjectResponse;

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
		@ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공", content = @Content()),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 요청 파라미터 오류", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@GetMapping
	ResponseEntity<PageResponse<ProjectResponse>> getProjects(
		@Parameter(hidden = true) Authentication authentication,
		@Parameter(description = "검색어; 프로젝트 제목만 검색가능", required = false) @RequestParam(required = false) String keyword,
		@Parameter(description = "페이징 정보", required = false) @RequestParam(required = false) PageRequest page
	);

	@Operation(
		summary = "[관리자] 프로젝트 생성",
		description = "(아울다 관리자 전용) 프로젝트를 생성합니다."
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
		@RequestBody CreateProjectRequest request
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
		@ApiResponse(responseCode = "200", description = "생성요청 목록 조회 성공", content = @Content()),
		@ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content()),
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

	// 5. [관리자] 신규 프로젝트 생성요청 승인/거절
	@Operation(
		summary = "[관리자] 신규 프로젝트 생성요청 승인/거절",
		description = "프로젝트에 대한 생성요청을 승인하거나 거절합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "요청 처리 성공", content = @Content()),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 - 처리 상태값 오류 등", content = @Content()),
		@ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰", content = @Content()),
		@ApiResponse(responseCode = "403", description = "권한 없음 - API 접근 권한이 없음", content = @Content()),
		@ApiResponse(responseCode = "500", description = "서버 오류 - 내부 서버 오류", content = @Content())
	})
	@PostMapping("/request")
	ResponseEntity<Void> decideProjectRequest(
		@Parameter(hidden = true) Authentication authentication,
		@RequestBody DecideProjectRequestRequest request
	);
}
