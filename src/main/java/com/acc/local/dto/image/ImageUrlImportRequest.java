package com.acc.local.dto.image;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이미지 URL Import 요청")
public record ImageUrlImportRequest(
        @Schema(description = "이미지 메타데이터")
        ImageMetadataRequest metadata,
        @Schema(description = "이미지 파일 URL")
        String fileUrl
) {}

