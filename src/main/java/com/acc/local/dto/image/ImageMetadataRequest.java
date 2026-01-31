package com.acc.local.dto.image;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이미지 메타데이터 요청")
public record ImageMetadataRequest(
        @Schema(description = "이미지 이름")
        String name,
        @Schema(description = "디스크 포맷")
        String diskFormat,
        @Schema(description = "컨테이너 포맷")
        String containerFormat,
        @Schema(description = "아키텍처")
        String architecture,
        @Schema(description = "최소 디스크 크기(GiB)")
        Integer minDisk,
        @Schema(description = "최소 메모리 크기(MiB)")
        Integer minRam
) {}