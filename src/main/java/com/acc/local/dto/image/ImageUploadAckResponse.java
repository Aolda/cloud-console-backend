package com.acc.local.dto.image;

import lombok.Builder;

@Builder
public record ImageUploadAckResponse(
        String imageId,        // 생성된 이미지 UUID (Glance가 반환)
        String name,           // 요청 시 입력한 이미지 이름
        String uploadMethod,   // FILE | URL (요청 타입)
        String status,         // Glance 초기 상태 (ex. "queued")
        String message         // 예: "Image import request accepted"
) {}