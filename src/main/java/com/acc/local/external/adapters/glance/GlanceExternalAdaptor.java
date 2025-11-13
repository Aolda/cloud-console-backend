package com.acc.local.external.adapters.glance;


import com.acc.local.dto.image.ImageDetailResponse;
import com.acc.local.dto.image.ImageMetadataRequest;
import com.acc.local.external.dto.glance.image.CreateImageRequest;
import com.acc.local.external.dto.glance.image.FetchImagesRequestParam;
import com.acc.local.external.dto.glance.image.ImportImageRequest;
import com.acc.local.external.modules.glance.GlanceImageAPIModule;
import com.acc.local.external.ports.GlanceExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GlanceExternalAdaptor implements GlanceExternalPort {
    private final GlanceImageAPIModule glanceImageAPIModule;

    @Override
    public ResponseEntity<JsonNode> fetchPrivateImageList(String token, String projectId) {
        FetchImagesRequestParam param = FetchImagesRequestParam.builder()
                .owner(projectId)
                .visibility("private")
                .limit(100)
                .sortKey("created_at")
                .sortDir("desc")
                .build();

        return glanceImageAPIModule.fetchImageList(token, param);
    }

    @Override
    public ResponseEntity<JsonNode> fetchPublicImageList(String token) {
        FetchImagesRequestParam param = FetchImagesRequestParam.builder()
                .visibility("public")
                .limit(100)
                .sortKey("created_at")
                .sortDir("desc")
                .build();

        return glanceImageAPIModule.fetchImageList(token, param);
    }

    @Override
    public ResponseEntity<JsonNode> fetchImageDetail(String token, String imageId) {
        return glanceImageAPIModule.fetchImage(token, imageId);
    }

    @Override
    public ResponseEntity<JsonNode> createImageMetadata(String token, ImageMetadataRequest req) {
        CreateImageRequest createReq = CreateImageRequest.builder()
                .name(req.name())
                .diskFormat(req.diskFormat())
                .containerFormat(req.containerFormat())
                .visibility("private")   // 관리자 기능 추가 시 인자로 추가
                .minDisk(req.minDisk())
                .minRam(req.minRam())
                .build();

        return glanceImageAPIModule.createImage(token, createReq);
    }

    @Override
    public void importImageUrl(String token, String imageId, String fileUrl) {
        ImportImageRequest importReq = ImportImageRequest.builder()
                .method(ImportImageRequest.Method.builder()
                        .name("web-download")
                        .uri(fileUrl)
                        .glanceImageId(imageId)
                        .glanceRegion("ToeHak")
                        .glanceServiceInterface("public")
                        .build())
                .allStores(true)
                .allStoresMustSucceed(false)
                .stores(List.of())
                .build();

        glanceImageAPIModule.importImage(token, imageId, importReq);
    }

    @Override
    public void deleteImage(String token, String imageId) {
        glanceImageAPIModule.deleteImage(token, imageId);
    }
}


