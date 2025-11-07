package com.acc.local.dto.volume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "볼륨 상세 정보")
public class VolumeResponse {

    @Schema(description = "볼륨 ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String volumeId;

    @Schema(description = "볼륨 이름", example = "my-volume")
    private String name;

    @Schema(description = "볼륨 크기(GiB)", example = "10")
    private Integer size;

    @Schema(description = "볼륨 상태", example = "available")
    private String status;

    @Schema(description = "볼륨 타입", example = "__DEFAULT__")
    private String volumeType;

    @Schema(description = "볼륨 설명", example = "My storage volume")
    private String description;

    @Schema(description = "가용 영역", example = "nova")
    private String availabilityZone;

    @Schema(description = "생성 시간 (UTC)", example = "2025-10-10T04:00:00.000000")
    private String createdAt;

    @Schema(description = "부팅 가능 여부", example = "false")
    private String bootable;
}