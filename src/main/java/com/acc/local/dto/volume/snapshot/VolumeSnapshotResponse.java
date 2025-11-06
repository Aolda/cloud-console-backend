package com.acc.local.dto.volume.snapshot;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@Schema(description = "볼륨 스냅샷 상세 정보")
public class VolumeSnapshotResponse {

    @Schema(description = "스냅샷 ID", example = "e1f2a3b4-c5d6-e7f8-a9b0-c1d2e3f4a5b6")
    private String snapshotId;

    @Schema(description = "스냅샷 이름", example = "my-daily-snapshot")
    private String name;

    @Schema(description = "스냅샷 크기(GiB)", example = "50")
    private int sizeGb;

    @Schema(description = "스냅샷 상태", example = "AVAILABLE")
    private String status;

    @Schema(description = "원본 볼륨 ID", example = "b8f6a3b2-9d3a-4a6e-8b1e-2e4a6d8c2e1f")
    private String sourceVolumeId;

    @Schema(description = "생성 시간 (UTC)", example = "2025-10-10T04:00:00.000000")
    private String createdAt;
}
