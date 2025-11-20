package com.acc.local.service.modules.image;

import com.acc.global.exception.image.ImageErrorCode;
import com.acc.global.exception.image.ImageException;
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
        try {
            ResponseEntity<JsonNode> createRes = glanceExternalPort.createImageMetadata(token, req.metadata());
            JsonNode body = createRes.getBody();
            if (body == null || body.get("id") == null)
                throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);

            String imageId = body.get("id").asText();

            glanceExternalPort.importImageUrl(token, imageId, req.fileUrl());

            return ImageUploadAckResponse.builder()
                    .imageId(imageId)
                    .message("Image import request accepted")
                    .build();

        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_IMPORT_FAILURE, e);
        }
    }
}
