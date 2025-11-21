package com.acc.local.controller.docs;

import com.acc.local.dto.snapshot.policy.SnapshotPolicyRequest;
import com.acc.local.dto.snapshot.policy.SnapshotPolicyResponse;
import com.acc.local.dto.snapshot.policy.SnapshotTaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Snapshot Policy", description = "스냅샷 수명관리자 정책 API")
@RequestMapping("/api/v1/snapshot-policies")
public interface SnapshotPolicyDocs {

    @Operation(
            summary = "등록된 스냅샷 수명관리자 정책 목록 조회",
            description = "이용자는 프로젝트 내 등록된 모든 스냅샷 수명관리자 정책 목록을 조회할 수 있다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정책 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content())
    })
    @GetMapping
    ResponseEntity<Page<SnapshotPolicyResponse>> getPolicies(
            @Parameter(description = "인증 토큰", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "페이지 정보", required = false)
            Pageable pageable
    );

    @Operation(
            summary = "스냅샷 수명관리자 정책 상세조회",
            description = "이용자는 특정 스냅샷 수명관리자 정책의 세부 정보를 조회할 수 있다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정책 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SnapshotPolicyResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content()),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음", content = @Content())
    })
    @GetMapping(params = "policyId")
    ResponseEntity<SnapshotPolicyResponse> getPolicyDetails(
            @Parameter(description = "인증 토큰", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "조회할 정책 ID", required = true)
            @RequestParam Long policyId
    );

    @Operation(
            summary = "스냅샷 수명관리자 정책 생성",
            description = "이용자는 새로운 스냅샷 수명관리자 정책을 생성할 수 있다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "정책 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SnapshotPolicyResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content()),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content())
    })
    @PostMapping
    ResponseEntity<SnapshotPolicyResponse> createPolicy(
            @Parameter(description = "인증 토큰", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "생성할 정책 정보", required = true)
            @RequestBody SnapshotPolicyRequest request
    );

    @Operation(
            summary = "기존 스냅샷 수명관리자 정책 수정",
            description = "이용자는 기존 스냅샷 수명관리자 정책의 내용을 수정할 수 있다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정책 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SnapshotPolicyResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content()),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content()),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음", content = @Content())
    })
    @PutMapping(params = "policyId")
    ResponseEntity<SnapshotPolicyResponse> updatePolicy(
            @Parameter(description = "인증 토큰", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "수정할 정책 ID", required = true)
            @RequestParam Long policyId,
            @Parameter(description = "수정할 정책 정보", required = true)
            @RequestBody SnapshotPolicyRequest request
    );

    @Operation(
            summary = "지정된 스냅샷 수명관리자 정책 삭제",
            description = "이용자는 생성된 기존 스냅샷 수명관리자 정책을 삭제할 수 있다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "정책 삭제 성공",
                    content = @Content()
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content()),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음", content = @Content())
    })
    @DeleteMapping(params = "policyId")
    ResponseEntity<Void> deletePolicy(
            @Parameter(description = "인증 토큰", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "삭제할 정책 ID", required = true)
            @RequestParam Long policyId
    );

    @Operation(
            summary = "정책 비활성화",
            description = "정책을 비활성화 상태로 전환"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정책 비활성화 성공",
                    content = @Content()
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content()),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음", content = @Content())
    })
    @PostMapping("/{policyId}/deactivate")
    ResponseEntity<Void> deactivatePolicy(
            @Parameter(description = "인증 토큰", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "비활성화할 정책 ID", required = true)
            @PathVariable Long policyId
    );

    @Operation(
            summary = "정책 활성화",
            description = "이용자는 특정 스냅샷 수명관리자 정책을 켜거나 끌 수 있다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정책 활성화 성공",
                    content = @Content()
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content()),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음", content = @Content())
    })
    @PostMapping("/{policyId}/activate")
    ResponseEntity<Void> activatePolicy(
            @Parameter(description = "인증 토큰", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "활성화할 정책 ID", required = true)
            @PathVariable Long policyId
    );

    @Operation(
            summary = "실행 이력 목록",
            description = "정책 실행 이력 조회(생성/삭제 개수, 상태 등)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "실행 이력 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content()),
            @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{policyId}/runs")
    ResponseEntity<Page<SnapshotTaskResponse>> getPolicyRuns(
            @Parameter(description = "인증 토큰", required = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "조회할 정책 ID", required = true)
            @PathVariable Long policyId,
            @Parameter(description = "조회 시작 일자 (YYYY-MM-DD)", required = false)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since,
            @Parameter(description = "페이지 정보", required = false)
            Pageable pageable
    );
}

