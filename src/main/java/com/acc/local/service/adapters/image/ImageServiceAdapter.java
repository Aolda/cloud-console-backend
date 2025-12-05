package com.acc.local.service.adapters.image;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.image.*;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.image.ImageServiceModule;
import com.acc.local.service.ports.ImageServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceAdapter implements ImageServicePort {

    private final ImageServiceModule imageServiceModule;
    private final AuthModule authModule;

    @Override
    public PageResponse<GlanceImageSummary> getImagesWithPagination(String userId, String projectId, PageRequest req, ImageFilterRequest imageFilterRequest) {
        String token = authModule.issueProjectScopeToken(projectId, userId);
        List<GlanceImageSummary> totalImageList = imageServiceModule.fetchSortedList(token, projectId, imageFilterRequest);
        return imageServiceModule.paginate(totalImageList, req);
    }

    @Override
    public ImageDetailResponse getImageDetail(String userId, String projectId, String imageId) {
        String token = authModule.issueProjectScopeToken(userId, projectId);
        return imageServiceModule.getImageDetail(token, imageId);
    }

    @Override
    public ImageUploadAckResponse importImageByUrl(String userId, String projectId, ImageUrlImportRequest request) {
        String token = authModule.issueProjectScopeToken(userId, projectId);
        return imageServiceModule.importImageByUrl(token, request);
    }

    @Override
    public ImageUploadAckResponse createImageMetadata(String userId, String projectId, ImageMetadataRequest req) {
        String token = authModule.issueProjectScopeToken(userId, projectId);
        return imageServiceModule.createImageMetadata(token, req);
    }

    @Override
    public void deleteImage(String userId, String projectId, String imageId) {
        String token = authModule.issueProjectScopeToken(userId, projectId);
        imageServiceModule.deleteImage(token, imageId);
    }

    @Override
    public void uploadFileStream(String userId, String projectId, String imageId, InputStream input, String contentType) {
        String token = authModule.issueProjectScopeToken(userId, projectId);
        imageServiceModule.uploadFileStream(token, imageId, input, contentType);
        authModule.invalidateServiceTokensByUserId(userId);
    }
}
