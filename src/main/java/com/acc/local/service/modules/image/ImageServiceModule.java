package com.acc.local.service.modules.image;

import com.acc.global.exception.image.ImageException;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.local.dto.image.*;
import com.acc.local.external.ports.GlanceExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ImageServiceModule {

    private final GlanceExternalPort glanceExternalPort;
    private final ImageJsonMapperModule mapper;
    private final ImageImportFlowModule importFlow;

    public ImageListResponse getPrivateImages(String token, String projectId) {
        try {
            ResponseEntity<JsonNode> res = glanceExternalPort.fetchPrivateImageList(token, projectId);
            return mapper.toImageListResponse(res.getBody());
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_LIST_FETCH_FAILURE, e);
        }
    }

    public ImageListResponse getPublicImages(String token) {
        try {
            ResponseEntity<JsonNode> res = glanceExternalPort.fetchPublicImageList(token);
            return mapper.toImageListResponse(res.getBody());
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_LIST_FETCH_FAILURE, e);
        }
    }

    public ImageDetailResponse getImageDetail(String token, String imageId) {
        try {
            ResponseEntity<JsonNode> res = glanceExternalPort.fetchImageDetail(token, imageId);
            return mapper.toImageDetailResponse(res.getBody());
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_DETAIL_FETCH_FAILURE, e);
        }
    }

    public ImageUploadAckResponse importImageByUrl(String token, ImageUrlImportRequest req) {
        try {
            return importFlow.executeUrlImport(token, req);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_IMPORT_FAILURE, e);
        }
    }

    public ImageUploadAckResponse createImageMetadata(String token, ImageMetadataRequest req) {
        try {
            ResponseEntity<JsonNode> res = glanceExternalPort.createImageMetadata(token, req);
            return mapper.toUploadAck(res.getBody());
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_METADATA_CREATE_FAILURE, e);
        }
    }

    public void deleteImage(String token, String imageId) {
        try {
            glanceExternalPort.deleteImage(token, imageId);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_DELETE_FAILURE, e);
        }
    }

    public void uploadFileStream(String token, String imageId, InputStream input, String contentType) {
        try {
            glanceExternalPort.uploadImageProxyStream(token, imageId, input, contentType);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_UPLOAD_FAILURE, e);
        }
    }
}
