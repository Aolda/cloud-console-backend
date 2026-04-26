package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.image.*;
import java.io.InputStream;

public interface ImageServicePort {

    PageResponse<GlanceImageSummary> getImagesWithPagination(String sessionId, String projectId, PageRequest pageRequest, ImageFilterRequest imageFilterRequest);

    ImageDetailResponse getImageDetail(String sessionId, String projectId, String imageId);

    ImageUploadAckResponse importImageByUrl(String sessionId, String projectId, ImageUrlImportRequest request);

    ImageUploadAckResponse createImageMetadata(String sessionId, String projectId, ImageMetadataRequest req);

    void deleteImage(String sessionId, String projectId, String imageId);

    void uploadFileStream(String sessionId, String projectId, String imageId, InputStream input, String contentType);
}
