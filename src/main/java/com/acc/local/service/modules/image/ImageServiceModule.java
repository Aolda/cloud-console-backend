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
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientException;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageServiceModule {

    private final GlanceExternalPort glanceExternalPort;
    private final ImageMapperUtil mapper;
    private final QuickStartProperties quickStartProperties;

    public List<GlanceImageSummary> fetchSortedList(String token, String projectId, ImageFilterRequest filters) {

        ResponseEntity<JsonNode> res;

        try {
            res = glanceExternalPort.fetchImageList(token, projectId, filters);
        } catch (RestClientException | WebClientException e) {
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE, e);
        }

        int status = res.getStatusCode().value();

        return switch (status) {
            case 200 -> {
                List<GlanceImageSummary> list = mapper.toImageListResponse(res.getBody());
                yield mapper.sortGlanceImageSummary(list);
            }
            case 403 -> throw new ImageException(ImageErrorCode.IMAGE_NOT_ACCESSIBLE);
            case 500, 502, 503 -> throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE);
            default -> throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE);
        };
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

        ResponseEntity<JsonNode> res;

        try {
            res = glanceExternalPort.fetchImageDetail(token, imageId);
        } catch (RestClientException | WebClientException e) {
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE, e);
        }

        int status = res.getStatusCode().value();

        return switch (status) {
            case 200 -> mapper.toImageDetailResponse(res.getBody());
            case 404 -> throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
            case 403 -> throw new ImageException(ImageErrorCode.IMAGE_NOT_ACCESSIBLE);
            case 500, 502, 503 -> throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE);
            default -> throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE);
        };
    }


    public ImageUploadAckResponse importImageByUrl(String token, ImageUrlImportRequest req) {

        // 1) Metadata 생성 -----------------------------------------
        ResponseEntity<JsonNode> createRes;
        try {
            createRes = glanceExternalPort.createImageMetadata(token, req.metadata());
        } catch (RestClientException | WebClientException e) {
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE, e);
        }

        int metaStatus = createRes.getStatusCode().value();
        String imageId;

        switch (metaStatus) {
            case 200, 201 -> {
                JsonNode body = createRes.getBody();
                if (body == null || body.get("id") == null)
                    throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);
                imageId = body.get("id").asText();
            }
            case 400 -> throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);
            case 403 -> throw new ImageException(ImageErrorCode.IMAGE_METADATA_CREATE_FORBIDDEN);
            case 500, 502, 503 -> throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE);
            default -> throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE);
        }

        // 2) URL Import + 실패 시 rollback --------------------------
        ResponseEntity<Void> importRes;
        try {
            importRes = glanceExternalPort.importImageUrl(token, imageId, req.fileUrl());
        } catch (RestClientException | WebClientException e) {
            safeDelete(token, imageId);
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        } catch (Exception e) {
            safeDelete(token, imageId);
            throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE, e);
        }

        int importStatus = importRes.getStatusCode().value();

        switch (importStatus) {

            case 200, 202, 204 -> { /* success */ }
            case 400 -> {
                //메타 데이터 삭제
                safeDelete(token, imageId);
                throw new ImageException(ImageErrorCode.INVALID_IMPORT_URL);
            }
            case 403, 409 -> throw new ImageException(ImageErrorCode.IMAGE_IMPORT_FAILURE);
            case 500, 502, 503 -> throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE);
            default -> throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE);
        }

        // 3) 최종 성공 ---------------------------------------------
        return ImageUploadAckResponse.builder()
                .imageId(imageId)
                .message("Image import request accepted")
                .build();
    }

    private void safeDelete(String token, String imageId) {
        try { glanceExternalPort.deleteImage(token, imageId); } catch (Exception ignore) {}
    }

    public ImageUploadAckResponse createImageMetadata(String token, ImageMetadataRequest req) {
        try {
            ResponseEntity<JsonNode> res = glanceExternalPort.createImageMetadata(token, req);
            return mapper.toUploadAck(res.getBody());
        } catch (Exception e) {
            // 엔드 포인트 호출 없는 메소드. 추후 사용 시 에러 대응 필요
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        }
    }

    public void deleteImage(String token, String imageId) {
        ResponseEntity<Void> res;

        try {
            res = glanceExternalPort.deleteImage(token, imageId);
        } catch (RestClientException | WebClientException e) {
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE, e);
        }

        int status = res.getStatusCode().value();

        switch (status) {
            case 204, 200 -> { return; }   // 성공
            case 404 -> throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
            case 403 -> throw new ImageException(ImageErrorCode.IMAGE_DELETE_FORBIDDEN);
            case 409 -> throw new ImageException(ImageErrorCode.IMAGE_STATUS_CONFLICT);
            case 500, 502, 503 -> throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE);
            default -> throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE);
        }
    }


    public void uploadFileStream(String token, String imageId, InputStream input, String contentType) {
        //엔드포인트 호출 없는 메소드
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
        JsonNode res;
        try {
            res = glanceExternalPort.fetchImageDetail(token, imageId).getBody();
            if (res == null) throw new ImageException(ImageErrorCode.INVALID_QUICK_START_IMAGE);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.INVALID_QUICK_START_IMAGE, e);
        }

        String status = res.path("status").asText(null);
        String os_distro = res.path("os_distro").asText(null);

        // Glance 이미지 상태가 ACTIVE가 아니거나, os_distro가 ubuntu가 아니면 에러 발생
        // env에 이미지 ID 조회 실패!! -> 추후 메일 알림 등 알림 시스템 필요
        if (!"active".equalsIgnoreCase(status) || !"ubuntu".equalsIgnoreCase(os_distro)) {
            throw new ImageException(ImageErrorCode.INVALID_QUICK_START_IMAGE);
        }

        return imageId;
    }
}
