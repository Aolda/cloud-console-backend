package com.acc.local.service.ports;

import com.acc.local.dto.image.*;
import java.io.InputStream;

public interface ImageServicePort {

    ImageListResponse getPrivateImages(String userId, String projectId);

    ImageListResponse getPublicImages(String userId, String projectId);

    ImageDetailResponse getImageDetail(String userId, String projectId, String imageId);

    ImageUploadAckResponse importImageByUrl(String userId, String projectId, ImageUrlImportRequest request);

    ImageUploadAckResponse createImageMetadata(String userId, String projectId, ImageMetadataRequest req);

    void deleteImage(String userId, String projectId, String imageId);

    void uploadFileStream(String userId, String projectId, String imageId, InputStream input, String contentType);
}
