package com.acc.local.service.modules.image;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.image.ImageException;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.global.properties.QuickStartProperties;
import com.acc.local.dto.image.*;
import com.acc.local.external.ports.GlanceExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageServiceModule {

    private final GlanceExternalPort glanceExternalPort;
    private final ImageJsonMapperModule mapper;
    private final QuickStartProperties quickStartProperties;

    public List<GlanceImageSummary> fetchSortedList(String token, String projectId, ImageFilterRequest filters) {
        try {
            ResponseEntity<JsonNode> res = glanceExternalPort.fetchImageList(token, projectId, filters);
            List<GlanceImageSummary> imageSummaryList = mapper.toImageListResponse(res.getBody());
            return mapper.sortGlanceImageSummary(imageSummaryList);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_LIST_FETCH_FAILURE, e);
        }
    }

    public PageResponse<GlanceImageSummary> paginate(List<GlanceImageSummary> all, PageRequest req) {
        String marker = req.getMarker();
        int limit = req.getLimit();
        PageRequest.Direction direction = req.getDirection();

        int startIdx = 0;

        if (marker != null) {
            int markerIndex = -1;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).id().equals(marker)) {
                    markerIndex = i;
                    break;
                }
            }

            if (markerIndex != -1) {
                if (direction == PageRequest.Direction.next) {
                    startIdx = markerIndex + 1;
                } else {
                    startIdx = Math.max(markerIndex - limit, 0);
                }
            }
        }

        int endIdx = Math.min(startIdx + limit, all.size());
        List<GlanceImageSummary> contents = all.subList(startIdx, endIdx);

        boolean first = (marker == null);
        boolean last = (endIdx >= all.size());

        String nextMarker = last ? null : contents.get(contents.size() - 1).id();
        String prevMarker = first ? null : contents.get(0).id();

        return PageResponse.<GlanceImageSummary>builder()
                .contents(contents)
                .first(first)
                .last(last)
                .size(contents.size())
                .nextMarker(nextMarker)
                .prevMarker(prevMarker)
                .build();
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
            ResponseEntity<JsonNode> createRes = glanceExternalPort.createImageMetadata(token, req.metadata());
            JsonNode body = createRes.getBody();
            if (body == null || body.get("id") == null) throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);

            String imageId = body.get("id").asText();

            glanceExternalPort.importImageUrl(token, imageId, req.fileUrl());

            return ImageUploadAckResponse.builder()
                    .imageId(imageId)
                    .message("Image import request accepted")
                    .build();
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

    //QuickStart 시 Default 이미지 가져오기 및 ID 유효성 검사
    //추후 아키텍처에 따라 baseImage 여러 개 관리 가능
    public String fetchQuickStartImageId(String token) {
        String imageId = quickStartProperties.getDefaultImageId();

        // external에서 token error or 403 or image not found는 Exception으로 처리 (예정)
        JsonNode res = glanceExternalPort.fetchImageDetail(token, imageId).getBody();

        if (res == null) {
            throw new ImageException(ImageErrorCode.INVALID_QUICK_START_IMAGE);
        }

        // 필드 파싱
        String status = res.path("status").asText(null);
        String os_distro = res.path("os_distro").asText(null);

        // Glance 이미지 상태가 ACTIVE가 아니거나, os_distro가 ubuntu가 아니면 에러 발생
        if (!"active".equalsIgnoreCase(status) || !"ubuntu".equalsIgnoreCase(os_distro)) {
            throw new ImageException(ImageErrorCode.INVALID_QUICK_START_IMAGE);
        }

        return imageId;
    }
}
