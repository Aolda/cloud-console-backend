package com.acc.local.external.ports;

import com.acc.local.dto.image.ImageFilterRequest;
import com.acc.local.dto.image.ImageMetadataRequest;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.Map;

public interface GlanceExternalPort {

    ResponseEntity<JsonNode> fetchImageList(String token, String projectId, ImageFilterRequest filters);

//    ResponseEntity<JsonNode> fetchPrivateImageList(String token, String projectId);
//
//    ResponseEntity<JsonNode> fetchPublicImageList(String token);

    ResponseEntity<JsonNode> fetchImageDetail(String token, String imageId);

    ResponseEntity<JsonNode> createImageMetadata(String token, ImageMetadataRequest req);

    ResponseEntity<Void> importImageUrl(String token, String imageId, String fileUrl);

    void uploadImageProxyStream(String token, String imageId, InputStream body, String contentType);

    ResponseEntity<Void> deleteImage(String token, String imageId);

//    ResponseEntity<JsonNode> fetchImage(String token, String imageId);
//
//    ResponseEntity<JsonNode> fetchImageList(String token, GlanceFetchImagesRequestParam params);
//
//    ResponseEntity<JsonNode> fetchImageTask(String token, String imageId);
//
//    ResponseEntity<JsonNode> createImage(String token, ImageUrlImportRequest request);
//
//    ResponseEntity<JsonNode> importImage(String token, String imageId, GlanceImportImageRequest request);
//
//    ResponseEntity<JsonNode> uploadImageFile(String token, String imageId, Resource resource);
//
//    ResponseEntity<JsonNode> updateImage(String token, String imageId, GlanceUpdateImageRequest request);
}
