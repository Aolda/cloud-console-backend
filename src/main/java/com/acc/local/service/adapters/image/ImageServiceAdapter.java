package com.acc.local.service.adapters.image;

import com.acc.global.exception.image.ImageException;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.local.dto.image.*;
import com.acc.local.external.ports.GlanceExternalPort;
import com.acc.local.service.modules.image.ImageJsonMapperModule;
import com.acc.local.service.ports.ImageServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageServiceAdapter implements ImageServicePort {

    private final GlanceExternalPort glanceExternalPort;
    private final ImageJsonMapperModule mapper;

    @Override
    public ImageListResponse getPrivateImages(String token, String projectId) {
        try {
            ResponseEntity<JsonNode> res = glanceExternalPort.fetchPrivateImageList(token, projectId);
            return mapper.toImageListResponse(res.getBody());
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_LIST_FETCH_FAILURE, e);
        }
    }

    @Override
    public ImageListResponse getPublicImages(String token) {
        try {
            ResponseEntity<JsonNode> res = glanceExternalPort.fetchPublicImageList(token);
            return mapper.toImageListResponse(res.getBody());
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_LIST_FETCH_FAILURE, e);
        }
    }

    @Override
    public ImageDetailResponse getImageDetail(String token, String imageId) {
        try {
            ResponseEntity<JsonNode> res = glanceExternalPort.fetchImageDetail(token, imageId);
            return mapper.toImageDetailResponse(res.getBody());
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_DETAIL_FETCH_FAILURE, e);
        }
    }

    @Override
    public ImageUploadAckResponse importImageByUrl(String token, ImageUrlImportRequest request) {
        try {
            ResponseEntity<JsonNode> createRes = glanceExternalPort.createImageMetadata(token, request.metadata());
            String createdImageId = extractId(createRes.getBody());
            glanceExternalPort.importImageUrl(token, createdImageId, request.fileUrl());
            return ImageUploadAckResponse.builder()
                    .imageId(createdImageId)
                    .name(request.metadata().name())
                    .status("queued")
                    .message("Image import request submitted.")
                    .build();
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_IMPORT_FAILURE, e);
        }
    }

    @Override
    public ImageUploadAckResponse createImageMetadata(String token, ImageMetadataRequest req) {
        try {
            ResponseEntity<JsonNode> res = glanceExternalPort.createImageMetadata(token, req);
            return mapper.toUploadAck(res.getBody());
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_METADATA_CREATE_FAILURE, e);
        }
    }

    @Override
    public void deleteImage(String token, String imageId) {
        try {
            glanceExternalPort.deleteImage(token, imageId);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_DELETE_FAILURE, e);
        }
    }

    @Override
    public void uploadFileStream(String token, String imageId, InputStream input, String contentType) {
        try {
            glanceExternalPort.uploadImageProxyStream(token, imageId, input, contentType);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_UPLOAD_FAILURE, e);
        }
    }

    private String extractId(JsonNode body) {
        if (body == null || body.get("id") == null) {
            throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);
        }
        return body.get("id").asText();
    }
}
