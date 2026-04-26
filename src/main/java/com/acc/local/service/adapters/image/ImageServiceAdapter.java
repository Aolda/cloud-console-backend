package com.acc.local.service.adapters.image;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.image.*;
import com.acc.local.service.modules.image.ImageServiceModule;
import com.acc.local.service.modules.session.SessionModule;
import com.acc.local.service.ports.ImageServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceAdapter implements ImageServicePort {

    private final ImageServiceModule imageServiceModule;
    private final SessionModule sessionModule;

    @Override
    public PageResponse<GlanceImageSummary> getImagesWithPagination(String sessionId, String projectId, PageRequest req, ImageFilterRequest imageFilterRequest) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        List<GlanceImageSummary> totalImageList = imageServiceModule.fetchSortedList(token, projectId, imageFilterRequest);
        return imageServiceModule.paginate(totalImageList, req);
    }

    @Override
    public ImageDetailResponse getImageDetail(String sessionId, String projectId, String imageId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        return imageServiceModule.getImageDetail(token, imageId);
    }

    @Override
    public ImageUploadAckResponse importImageByUrl(String sessionId, String projectId, ImageUrlImportRequest request) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        return imageServiceModule.importImageByUrl(token, request);
    }

    @Override
    public ImageUploadAckResponse createImageMetadata(String sessionId, String projectId, ImageMetadataRequest req) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        return imageServiceModule.createImageMetadata(token, req);
    }

    @Override
    public void deleteImage(String sessionId, String projectId, String imageId) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        imageServiceModule.deleteImage(token, imageId);
    }

    @Override
    public void uploadFileStream(String sessionId, String projectId, String imageId, InputStream input, String contentType) {
        String token = sessionModule.getKeystoneScopedToken(sessionId, projectId);
        imageServiceModule.uploadFileStream(token, imageId, input, contentType);
    }
}
