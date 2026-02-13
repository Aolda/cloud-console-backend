package com.acc.local.dto.image;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이미지 업로드/Import 응답")
public record ImageUploadAckResponse(
        @Schema(description = "생성된 이미지 ID")
        String imageId,
        @Schema(description = "이미지 이름")
        String name,
        @Schema(description = "업로드 방식 (FILE/URL)")
        String uploadMethod,
        @Schema(description = "이미지 상태")
        String status,
        @Schema(description = "응답 메시지")
        String message
) {}