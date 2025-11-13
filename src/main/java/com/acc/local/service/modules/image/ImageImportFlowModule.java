package com.acc.local.service.modules.image;

import com.acc.local.dto.image.ImageMetadataRequest;
import com.acc.local.dto.image.ImageUploadAckResponse;
import com.acc.local.dto.image.ImageUrlImportRequest;
import com.acc.local.external.ports.GlanceExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageImportFlowModule {

    private final GlanceExternalPort glanceExternalPort;

    public ImageUploadAckResponse executeUrlImport(String token, ImageUrlImportRequest req) {

        // 1) 메타데이터 생성
        ResponseEntity<JsonNode> createRes = glanceExternalPort.createImageMetadata(token, req.metadata());

        String imageId = createRes.getBody().get("id").asText();

        // 2) URL import 실행
        glanceExternalPort.importImageUrl(token, imageId, req.fileUrl());

        // 3) 결과 Ack 반환
        return ImageUploadAckResponse.builder()
                .imageId(imageId)
                .message("Image import request accepted")
                .build();
    }
}
