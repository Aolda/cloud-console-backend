package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.image.*;
import java.io.InputStream;

public interface ImageServicePort {

    PageResponse<ImageListResponse.GlanceImageSummary> getImagesWithPagination(String userId, String projectId, PageRequest pageRequest);

    ImageDetailResponse getImageDetail(String userId, String projectId, String imageId);

    ImageUploadAckResponse importImageByUrl(String userId, String projectId, ImageUrlImportRequest request);

    ImageUploadAckResponse createImageMetadata(String userId, String projectId, ImageMetadataRequest req);

    void deleteImage(String userId, String projectId, String imageId);

    void uploadFileStream(String userId, String projectId, String imageId, InputStream input, String contentType);
}
