package com.acc.local.controller;

import com.acc.local.dto.volume.snapshot.VolumeSnapshotsResponse;
import com.acc.local.service.ports.VolumeSnapshotServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/volume-snapshot")
@Tag(name = "Volume Snapshot", description = "볼륨 스냅샷 관련 API")
public class VolumeSnapshotController {

    private final VolumeSnapshotServicePort volumeSnapshotServicePort;
    @GetMapping
    @Operation(
            summary = "볼륨 스냅샷 목록 조회",
            description = "특정 프로젝트에 속한 모든 볼륨 스냅샷 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "스냅샷 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VolumeSnapshotsResponse.class)
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
            )
    })
    public ResponseEntity<VolumeSnapshotsResponse> getSnapshots(
            @Parameter(description = "인증토큰", required = true, example = "{access_token}")
            @RequestHeader("X-Auth-Token") String token
            //@Parameter(description = "프로젝트 ID", required = true, example = "95282c49df5d47f68bb79bf4ad63a69b")
            //@PathVariable String projectId
    ) {
        return ResponseEntity.ok(volumeSnapshotServicePort.getSnapshots(token));
    }


    @DeleteMapping
    @Operation(
            summary = "볼륨 스냅샷 삭제",
            description = "지정된 스냅샷 ID에 해당하는 볼륨 스냅샷을 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "스냅샷 삭제 성공(No Content)",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "202",
                    description = "스냅샷 삭제 요청 접수 (Accepted, 비동기)",
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
                    description = "요청한 스냅샷 ID를 찾을 수 없음",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌 - 스냅샷이 사용 중이거나 삭제 불가능한 상태임",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content()
            )
    })
    public ResponseEntity<Void> deleteSnapshot(
            @Parameter(description = "인증 토큰", required = true, example = "{access_token}")
            @RequestHeader("X-Auth-Token") String token,
            @Parameter(description = "삭제할 볼륨 스냅샷 ID", required = true, example = "92f53cf1-ae3e-4cc2-b5b7-462201a2d847")
            @RequestParam String snapshotId) {

        return volumeSnapshotServicePort.deleteSnapshot(token, snapshotId);
    }

}
