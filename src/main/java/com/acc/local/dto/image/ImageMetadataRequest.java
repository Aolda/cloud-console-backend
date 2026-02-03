package com.acc.local.dto.image;

import com.acc.local.domain.enums.ContainerFormat;
import com.acc.local.domain.enums.DiskFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이미지 메타데이터 요청")
public record ImageMetadataRequest(
        @Schema(description = "이미지 이름", example = "ubuntu-22.04")
        String name,
        @Schema(description = "디스크 포맷")
        DiskFormat diskFormat,
        @Schema(description = "컨테이너 포맷")
        ContainerFormat containerFormat,
        @Schema(description = "아키텍처", example = "x86_64")
        String architecture,
        @Schema(description = "최소 디스크 크기(GiB)", example = "20")
        Integer minDisk,
        @Schema(description = "최소 메모리 크기(MiB)", example = "2048")
        Integer minRam
) {}