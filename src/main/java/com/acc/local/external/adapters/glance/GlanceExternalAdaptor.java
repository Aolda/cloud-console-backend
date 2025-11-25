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
        try {
            GlanceFetchImagesRequestParam param = requestParamMapper(filters);
            return glanceImageAPIModule.fetchImageList(token, param);
        } catch (Exception e) {
            System.out.println("External: fetchImageList error");
            System.out.println(e.getMessage());
            throw new ImageException(ImageErrorCode.IMAGE_LIST_FETCH_FAILURE, e);
        }
    }

//    @Override
//    public ResponseEntity<JsonNode> fetchPrivateImageList(String token, String projectId) {
//        try {
//            GlanceFetchImagesRequestParam param = GlanceFetchImagesRequestParam.builder()
//                    .owner(projectId)
//                    .visibility("private")
//                    .limit(100)
//                    .sortKey("created_at")
//                    .sortDir("desc")
//                    .build();
//
//            return glanceImageAPIModule.fetchImageList(token, param);
//        } catch (Exception e) {
////            System.out.println("fetchPrivateImageList error");
////            System.out.println(e.getMessage());
//            throw new ImageException(ImageErrorCode.IMAGE_LIST_FETCH_FAILURE, e);
//        }
//    }
//
//    @Override
//    public ResponseEntity<JsonNode> fetchPublicImageList(String token) {
//        try {
//            GlanceFetchImagesRequestParam param = GlanceFetchImagesRequestParam.builder()
//                    .visibility("public")
//                    .limit(100)
//                    .sortKey("created_at")
//                    .sortDir("desc")
//                    .build();
//
//            return glanceImageAPIModule.fetchImageList(token, param);
//        } catch (Exception e) {
////            System.out.println("fetchPublicImageList error");
//            throw new ImageException(ImageErrorCode.IMAGE_LIST_FETCH_FAILURE, e);
//        }
//    }

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
        } catch (Exception e) {
//            System.out.println("createImageMetadata error");
            throw new ImageException(ImageErrorCode.IMAGE_METADATA_CREATE_FAILURE, e);
        }
    }

    @Override
    public void importImageUrl(String token, String imageId, String fileUrl) {
        try {
            GlanceImportImageRequest importReq = GlanceImportImageRequest.builder()
                    .method(GlanceImportImageRequest.Method.builder()
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
