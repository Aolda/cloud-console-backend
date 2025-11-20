package com.acc.local.service.adapters.image;

import com.acc.local.dto.image.*;
import com.acc.local.service.modules.auth.AuthModule;
import com.acc.local.service.modules.image.ImageServiceModule;
import com.acc.local.service.ports.ImageServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageServiceAdapter implements ImageServicePort {

    private final ImageServiceModule imageServiceModule;
    private final AuthModule authModule;

    private String acquireProjectScopedToken(String userId, String projectId) {
        return authModule.issueProjectScopeToken(userId, projectId);
    }

    private void invalidateTokens(String userId) {
        authModule.invalidateServiceTokensByUserId(userId);
    }

    @Override
    public ImageListResponse getPrivateImages(String userId, String projectId) {
        String token = acquireProjectScopedToken(userId, projectId);
        try {
            return imageServiceModule.getPrivateImages(token, projectId);
        } finally {
            invalidateTokens(userId);
        }
    }

    @Override
    public ImageListResponse getPublicImages(String userId, String projectId) {
        String token = acquireProjectScopedToken(userId, projectId);
        try {
            return imageServiceModule.getPublicImages(token);
        } finally {
            invalidateTokens(userId);
        }
    }

    @Override
    public ImageDetailResponse getImageDetail(String userId, String projectId, String imageId) {
        String token = acquireProjectScopedToken(userId, projectId);
        try {
            return imageServiceModule.getImageDetail(token, imageId);
        } finally {
            invalidateTokens(userId);
        }
    }

    @Override
    public ImageUploadAckResponse importImageByUrl(String userId, String projectId, ImageUrlImportRequest request) {
        String token = acquireProjectScopedToken(userId, projectId);
        try {
            return imageServiceModule.importImageByUrl(token, request);
        } finally {
            invalidateTokens(userId);
        }
    }

    @Override
    public ImageUploadAckResponse createImageMetadata(String userId, String projectId, ImageMetadataRequest req) {
        String token = acquireProjectScopedToken(userId, projectId);
        try {
            return imageServiceModule.createImageMetadata(token, req);
        } finally {
            invalidateTokens(userId);
        }
    }

    @Override
    public void deleteImage(String userId, String projectId, String imageId) {
        String token = acquireProjectScopedToken(userId, projectId);
        try {
            imageServiceModule.deleteImage(token, imageId);
        } finally {
            invalidateTokens(userId);
        }
    }

    @Override
    public void uploadFileStream(String userId, String projectId, String imageId, InputStream input, String contentType) {
        String token = acquireProjectScopedToken(userId, projectId);
        try {
            imageServiceModule.uploadFileStream(token, imageId, input, contentType);
        } finally {
            invalidateTokens(userId);
        }
    }
}
