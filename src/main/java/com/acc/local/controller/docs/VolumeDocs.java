package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.volume.VolumeRequest;
import com.acc.local.dto.volume.VolumeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Volume", description = "볼륨 API")
@RequestMapping("/api/v1/volumes")
public interface VolumeDocs {

    @Operation(
            summary = "볼륨 목록 조회",
            description = "특정 프로젝트에 속한 모든 볼륨 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "볼륨 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content()
            )
    })
    @GetMapping
    ResponseEntity<PageResponse<VolumeResponse>> getVolumes(
            @Parameter(description = "페이지 정보", required = false)
            PageRequest page,
            @Parameter(hidden = true)
            Authentication authentication);

    @Operation(
            summary = "볼륨 상세 조회",
            description = "지정된 볼륨 ID에 해당하는 단일 볼륨의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "볼륨 상세 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VolumeResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content()),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content()),
            @ApiResponse(responseCode = "404", description = "볼륨을 찾을 수 없음", content = @Content())
    })
    @GetMapping("/{volumeId}")
    ResponseEntity<VolumeResponse> getVolumeDetails(
            @Parameter(description = "조회할 볼륨 ID", required = true)
            @PathVariable String volumeId,
            @Parameter(hidden = true)
            Authentication authentication
    );

    @Operation(
            summary = "볼륨 삭제",
            description = "지정된 볼륨 ID에 해당하는 볼륨을 삭제합니다."
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
                    description = "요청한 볼륨 ID를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌 - 볼륨이 사용 중이거나 삭제 불가능한 상태임",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content()
            )
    })
    @DeleteMapping("/{volumeId}")
    ResponseEntity<Void> deleteVolume(
            @Parameter(description = "삭제할 볼륨 ID", required = true, example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String volumeId,
            @Parameter(hidden = true)
            Authentication authentication
    );

    @Operation(
            summary = "볼륨 생성",
            description = "새 볼륨을 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202",
                    description = "볼륨 생성 요청 접수 (Accepted)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VolumeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - (예: 볼륨 크기 누락, 유효하지 않은 파라미터)",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌 - (예: 볼륨 쿼터 초과)",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 (Cinder API 실패)",
                    content = @Content()
            )
    })
    @PostMapping
    ResponseEntity<VolumeResponse> createVolume(
            @Parameter(description = "생성할 볼륨 정보", required = true)
            @RequestBody VolumeRequest request,
            @Parameter(hidden = true)
            Authentication authentication
    );
}
