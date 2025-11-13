package com.acc.local.service.modules.image;

import com.acc.local.dto.image.*;
import com.acc.local.external.ports.GlanceExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageServiceModule {

    private final GlanceExternalPort glanceExternalPort;
    private final ImageJsonMapperModule imageJsonMapperModule;
    private final ImageImportFlowModule imageImportFlowModule;

    public ImageListResponse getPrivateImages(String token, String projectId) {
        ResponseEntity<JsonNode> res = glanceExternalPort.fetchPrivateImageList(token, projectId);
        return imageJsonMapperModule.toImageListResponse(res.getBody());
    }

    public ImageListResponse getPublicImages(String token) {
        ResponseEntity<JsonNode> res = glanceExternalPort.fetchPublicImageList(token);
        return imageJsonMapperModule.toImageListResponse(res.getBody());
    }

    public ImageDetailResponse getImageDetail(String token, String imageId) {
        ResponseEntity<JsonNode> res =
                glanceExternalPort.fetchImageDetail(token, imageId);
        return imageJsonMapperModule.toImageDetailResponse(res.getBody());
    }

    public ImageUploadAckResponse importImageByUrl(String token, ImageUrlImportRequest req) {
        return imageImportFlowModule.executeUrlImport(token, req);
    }

    public void deleteImage(String token, String imageId) {
        glanceExternalPort.deleteImage(token, imageId);
    }
}
