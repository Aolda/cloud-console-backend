package com.acc.local.service.ports;

import com.acc.local.dto.image.*;

import java.io.InputStream;

public interface ImageServicePort {

    ImageListResponse getPrivateImages(String token, String projectId);

    ImageListResponse getPublicImages(String token);

    ImageDetailResponse getImageDetail(String token, String imageId);

    ImageUploadAckResponse importImageByUrl(String token, ImageUrlImportRequest request);

    ImageUploadAckResponse createImageMetadata(String token, ImageMetadataRequest req);

    void deleteImage(String token, String imageId);

    void uploadFileStream(String token, String imageId, InputStream input, String contentType);
}