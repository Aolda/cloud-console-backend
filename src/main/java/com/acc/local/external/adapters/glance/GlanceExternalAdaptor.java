package com.acc.local.external.adapters.glance;

import com.acc.global.exception.image.ImageErrorCode;
import com.acc.global.exception.image.ImageException;
import com.acc.local.dto.image.ImageFilterRequest;
import com.acc.local.dto.image.ImageMetadataRequest;
import com.acc.local.external.dto.glance.image.GlanceCreateImageRequest;
import com.acc.local.external.dto.glance.image.GlanceFetchImagesRequestParam;
import com.acc.local.external.dto.glance.image.GlanceImportImageRequest;
import com.acc.local.external.dto.glance.response.GlanceImageResponse;
import com.acc.local.external.modules.glance.GlanceImageAPIModule;
import com.acc.local.external.dto.glance.response.GlanceImagesResponse;
import com.acc.local.external.ports.GlanceExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class GlanceExternalAdaptor implements GlanceExternalPort {
    private final GlanceImageAPIModule glanceImageAPIModule;

    @Override
    public ResponseEntity<GlanceImagesResponse> fetchImageList(String token, String projectId, ImageFilterRequest filters) {
        GlanceFetchImagesRequestParam param = requestParamMapper(filters);
        try {
            return glanceImageAPIModule.fetchImageList(token, param);
        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            switch (status) {
                case 403 -> throw new ImageException(ImageErrorCode.IMAGE_NOT_ACCESSIBLE, e);
                case 500, 502, 503 -> throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
                default -> throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE, e);
            }
        } catch (RestClientException | WebClientException e) {
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        }
    }

    @Override
    public ResponseEntity<GlanceImageResponse> fetchImageDetail(String token, String imageId) {
        try {
            return glanceImageAPIModule.fetchImage(token, imageId);
        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            switch (status) {
                case 404 -> throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND, e);
                case 403 -> throw new ImageException(ImageErrorCode.IMAGE_NOT_ACCESSIBLE, e);
                case 500, 502, 503 -> throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
                default -> throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE, e);
            }
        } catch (RestClientException | WebClientException e) {
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        }
    }

    @Override
    public ResponseEntity<JsonNode> createImageMetadata(String token, ImageMetadataRequest req) {
        GlanceCreateImageRequest createReq = GlanceCreateImageRequest.builder()
                .name(req.name())
                .diskFormat(req.diskFormat())
                .containerFormat(req.containerFormat())
                .architecture(req.architecture())
                .visibility("private")
                .minDisk(req.minDisk())
                .minRam(req.minRam())
                .build();

        try {
            return glanceImageAPIModule.createImage(token, createReq);
        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            switch (status) {
                case 400 -> throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA, e);
                case 403 -> throw new ImageException(ImageErrorCode.IMAGE_METADATA_CREATE_FORBIDDEN, e);
                case 500, 502, 503 -> throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
                default -> throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE, e);
            }
        } catch (RestClientException | WebClientException e) {
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        }
    }

    @Override
    public ResponseEntity<Void> importImageUrl(String token, String imageId, String fileUrl) {
        GlanceImportImageRequest importReq = GlanceImportImageRequest.builder()
                .method(GlanceImportImageRequest.Method.builder()
                        .name("web-download")
                        .uri(fileUrl)
                        .build())
                .allStores(true)
                .allStoresMustSucceed(false)
                .build();

        try {
            return glanceImageAPIModule.importImage(token, imageId, importReq);
        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            switch (status) {
                case 400 -> throw new ImageException(ImageErrorCode.INVALID_IMPORT_URL, e);
                case 403, 409 -> throw new ImageException(ImageErrorCode.IMAGE_IMPORT_FAILURE, e);
                case 500, 502, 503 -> throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
                default -> throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE, e);
            }
        } catch (RestClientException | WebClientException e) {
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        }
    }

    @Override
    public void uploadImageProxyStream(String token, String imageId, InputStream body, String contentType) {
        InputStreamResource resource = new InputStreamResource(body) {
            @Override
            public long contentLength() {
                return -1;
            }

            @Override
            public String getFilename() {
                return null;
            }
        };

        try {
            glanceImageAPIModule.uploadImageFileStream(token, imageId, resource, contentType);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_UPLOAD_FAILURE, e);
        }
    }

    @Override
    public ResponseEntity<Void> deleteImage(String token, String imageId) {
        try {
            return glanceImageAPIModule.deleteImage(token, imageId);
        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            switch (status) {
                case 404 -> throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND, e);
                case 403 -> throw new ImageException(ImageErrorCode.IMAGE_DELETE_FORBIDDEN, e);
                case 409 -> throw new ImageException(ImageErrorCode.IMAGE_STATUS_CONFLICT, e);
                case 500, 502, 503 -> throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
                default -> throw new ImageException(ImageErrorCode.GLANCE_BAD_RESPONSE, e);
            }
        } catch (RestClientException | WebClientException e) {
            throw new ImageException(ImageErrorCode.GLANCE_UNAVAILABLE, e);
        }
    }

    private GlanceFetchImagesRequestParam requestParamMapper(ImageFilterRequest filterRequest) {
        //필터 규칙
        GlanceFetchImagesRequestParam.GlanceFetchImagesRequestParamBuilder builder = GlanceFetchImagesRequestParam.builder();

        //default -> os_hidden = false 만 조회
        if (!Boolean.TRUE.equals(filterRequest.getHidden())) {
            // null 또는 false일 경우 → os_hidden=false 필터
            builder.hidden(false);
        }

        //default -> active만 조회
        if (!Boolean.FALSE.equals(filterRequest.getIsActive())){
            // null 또는 false일 경우 → status=active 필터
            builder.status("active");
        }

        // default -> 필터 없음
        if (filterRequest.getVisibility() != null) {
            builder.visibility(filterRequest.getVisibility());
        }

        // default -> 필터 없음
        if (filterRequest.getName() != null) {
            builder.name(filterRequest.getName());
        }

        // default -> 필터 없음
        if (filterRequest.getArchitecture() != null) {
            builder.architecture(filterRequest.getArchitecture());
        }

        return builder.build();
    }

}
