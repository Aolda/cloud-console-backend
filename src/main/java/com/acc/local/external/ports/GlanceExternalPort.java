package com.acc.local.external.ports;

import com.acc.local.dto.image.ImageDetailResponse;
import com.acc.local.dto.image.ImageMetadataRequest;
import com.acc.local.external.dto.glance.image.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface GlanceExternalPort {

    ResponseEntity<JsonNode> fetchPrivateImageList(String token, String projectId);
    ResponseEntity<JsonNode> fetchPublicImageList(String token);
    ResponseEntity<JsonNode> fetchImageDetail(String token, String imageId);

    ResponseEntity<JsonNode> createImageMetadata(String token, ImageMetadataRequest req);
    void importImageUrl(String token, String imageId, String fileUrl);

    void deleteImage(String token, String imageId);

//    ResponseEntity<JsonNode> fetchImage(String token, String imageId);
//
//    ResponseEntity<JsonNode> fetchImageList(String token, FetchImagesRequestParam params);
//
//    ResponseEntity<JsonNode> fetchImageTask(String token, String imageId);
//
//    ResponseEntity<JsonNode> createImage(String token, ImageUrlImportRequest request);
//
//    ResponseEntity<JsonNode> importImage(String token, String imageId, ImportImageRequest request);
//
//    ResponseEntity<JsonNode> uploadImageFile(String token, String imageId, Resource resource);
//
//    ResponseEntity<JsonNode> updateImage(String token, String imageId, UpdateImageRequest request);
}
