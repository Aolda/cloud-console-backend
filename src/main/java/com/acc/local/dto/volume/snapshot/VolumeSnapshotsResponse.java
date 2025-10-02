package com.acc.local.dto.volume.snapshot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class VolumeSnapshotsResponse {

    @Schema(description = "볼륨 스냅샷 목록")
    List<VolumeSnapshot> VolumeSnapshots;

    @Getter
    @Setter
    @Builder
    public static class VolumeSnapshot{
        @Schema(description = "스냅샷 ID", example = "602d131f-0b3a-4f51-8b2a-892f3928178a")
        String id;

        @Schema(description = "스냅샷 이름", example = "my-volume-snapshot")
        String name;

        @Schema(description = "생성 시간", example = "2025.08.12 20:20")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm")
        LocalDateTime createdAt;

        @Schema(description = "볼륨 ID", example = "d9f8d1e2-b5c4-4a3e-b7d8-1b2c3d4e5f6a")
        String volumeId;

        @Schema(description = "스냅샷 상태", example = "available")
        String status;

        @Schema(description = "스냅샷 크기(GiB)", example = "200")
        int size;
    }
}
