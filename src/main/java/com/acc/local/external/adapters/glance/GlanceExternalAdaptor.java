package com.acc.local.external.adapters.glance;

import com.acc.global.exception.image.ImageException;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.local.dto.image.ImageFilterRequest;
import com.acc.local.dto.image.ImageMetadataRequest;
import com.acc.local.external.dto.glance.image.GlanceCreateImageRequest;
import com.acc.local.external.dto.glance.image.GlanceFetchImagesRequestParam;
import com.acc.local.external.dto.glance.image.GlanceImportImageRequest;
import com.acc.local.external.modules.glance.GlanceImageAPIModule;
import com.acc.local.external.ports.GlanceExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class GlanceExternalAdaptor implements GlanceExternalPort {
    private final GlanceImageAPIModule glanceImageAPIModule;

    @Override
    public ResponseEntity<JsonNode> fetchImageList(String token, String projectId, ImageFilterRequest filters) {
        GlanceFetchImagesRequestParam param = requestParamMapper(filters);
        return glanceImageAPIModule.fetchImageList(token, param);
    }

    @Override
    public ResponseEntity<JsonNode> fetchImageDetail(String token, String imageId) {
        return glanceImageAPIModule.fetchImage(token, imageId);
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

        return glanceImageAPIModule.createImage(token, createReq);
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

        return glanceImageAPIModule.importImage(token, imageId, importReq);
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

        glanceImageAPIModule.uploadImageFileStream(token, imageId, resource, contentType);
    }

    @Override
    public ResponseEntity<Void> deleteImage(String token, String imageId) {
        return glanceImageAPIModule.deleteImage(token, imageId);
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
