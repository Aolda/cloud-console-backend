package com.acc.local.external.adapters.glance;

import com.acc.global.exception.image.ImageException;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.local.dto.image.ImageMetadataRequest;
import com.acc.local.external.dto.glance.image.CreateImageRequest;
import com.acc.local.external.dto.glance.image.FetchImagesRequestParam;
import com.acc.local.external.dto.glance.image.ImportImageRequest;
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
    public ResponseEntity<JsonNode> fetchPrivateImageList(String token, String projectId) {
        try {
            FetchImagesRequestParam param = FetchImagesRequestParam.builder()
                    .owner(projectId)
                    .visibility("private")
                    .limit(100)
                    .sortKey("created_at")
                    .sortDir("desc")
                    .build();

            return glanceImageAPIModule.fetchImageList(token, param);
        } catch (Exception e) {
//            System.out.println("fetchPrivateImageList error");
//            System.out.println(e.getMessage());
            throw new ImageException(ImageErrorCode.IMAGE_LIST_FETCH_FAILURE, e);
        }
    }

    @Override
    public ResponseEntity<JsonNode> fetchPublicImageList(String token) {
        try {
            FetchImagesRequestParam param = FetchImagesRequestParam.builder()
                    .visibility("public")
                    .limit(100)
                    .sortKey("created_at")
                    .sortDir("desc")
                    .build();

            return glanceImageAPIModule.fetchImageList(token, param);
        } catch (Exception e) {
//            System.out.println("fetchPublicImageList error");
            throw new ImageException(ImageErrorCode.IMAGE_LIST_FETCH_FAILURE, e);
        }
    }

    @Override
    public ResponseEntity<JsonNode> fetchImageDetail(String token, String imageId) {
        try {
            return glanceImageAPIModule.fetchImage(token, imageId);
        } catch (Exception e) {
//            System.out.println("fetchImageDetail error");
            throw new ImageException(ImageErrorCode.IMAGE_DETAIL_FETCH_FAILURE, e);
        }
    }

    @Override
    public ResponseEntity<JsonNode> createImageMetadata(String token, ImageMetadataRequest req) {
        try {
            CreateImageRequest createReq = CreateImageRequest.builder()
                    .name(req.name())
                    .diskFormat(req.diskFormat())
                    .containerFormat(req.containerFormat())
                    .visibility("private")
                    .minDisk(req.minDisk())
                    .minRam(req.minRam())
                    .build();

            return glanceImageAPIModule.createImage(token, createReq);
        } catch (Exception e) {
//            System.out.println("createImageMetadata error");
            throw new ImageException(ImageErrorCode.IMAGE_METADATA_CREATE_FAILURE, e);
        }
    }

    @Override
    public void importImageUrl(String token, String imageId, String fileUrl) {
        try {
            ImportImageRequest importReq = ImportImageRequest.builder()
                    .method(ImportImageRequest.Method.builder()
                            .name("web-download")
                            .uri(fileUrl)
                            .build())
                    .allStores(true)
                    .allStoresMustSucceed(false)
                    .build();
            glanceImageAPIModule.importImage(token, imageId, importReq);
        } catch (Exception e) {
//            System.out.println("importImageUrl error");
//            System.out.println(e.getMessage());
            throw new ImageException(ImageErrorCode.IMAGE_IMPORT_FAILURE);
        }
    }

    @Override
    public void uploadImageProxyStream(String token, String imageId, InputStream body, String contentType) {
        try {
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
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_UPLOAD_FAILURE, e);
        }
    }

    @Override
    public void deleteImage(String token, String imageId) {
        try {
            glanceImageAPIModule.deleteImage(token, imageId);
        } catch (Exception e) {
//            System.out.println("deleteImage error");
//            System.out.println(e.getMessage());
            throw new ImageException(ImageErrorCode.IMAGE_DELETE_FAILURE, e);
        }
    }
}
