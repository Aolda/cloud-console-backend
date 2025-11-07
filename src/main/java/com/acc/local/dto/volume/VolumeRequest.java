package com.acc.local.dto.volume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VolumeRequest {
    @Schema(description = "볼륨 이름(선택)", example = "my-volume", required = false)
    private String name;

    @Schema(description = "볼륨 크기(GiB, 필수)", example = "10", required = true)
    private Integer size;

    @Schema(description = "볼륨 타입(선택)", example = "__DEFAULT__", required = false)
    private String volumeType;

    @Schema(description = "볼륨 설명(선택)", example = "My storage volume", required = false)
    private String description;

    @Schema(description = "가용 영역(선택)", example = "nova", required = false)
    private String availabilityZone;
}