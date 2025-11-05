package com.acc.local.dto.volume.snapshot;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class VolumeSnapshotRequest {
    @Schema(description = "원본 볼륨 ID", example = "b8f6a3b2-9d3a-4a6e-8b1e-2e4a6d8c2e1f")
    private String sourceVolumeId;
    @Schema(description = "스냅샷 이름", example = "my-daily-snapshot")
    private String name;
}
