package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.global.exception.image.ImageException;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.controller.docs.ImageDocs;
import com.acc.local.dto.image.*;
import com.acc.local.dto.image.ImageListResponse.GlanceImageSummary;
import com.acc.local.service.ports.ImageServicePort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController implements ImageDocs {

    private final ImageServicePort imageServicePort;

    @GetMapping
    public ResponseEntity<?> getImages(
            Authentication authentication,
            @RequestParam(value = "imageId", required = false) String imageId,
            @RequestParam(value = "hidden", required = false) boolean isHidden,
            @ModelAttribute PageRequest pageRequest
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        if (imageId != null) {
            ImageDetailResponse detail = imageServicePort.getImageDetail(userId, projectId, imageId);
            return ResponseEntity.ok(detail);
        }

        PageResponse<GlanceImageSummary> page = imageServicePort.getImagesWithPagination(userId, projectId, pageRequest, isHidden);
        return ResponseEntity.ok(page);
    }


    @PostMapping("/import")
    public ResponseEntity<ImageUploadAckResponse> importImageByUrl(
            Authentication authentication,
            @RequestBody ImageUrlImportRequest request
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        ImageUploadAckResponse res =
                imageServicePort.importImageByUrl(userId, projectId, request);

        return ResponseEntity.ok(res);
    }


    @Deprecated
    @PostMapping("/metadata")
    public ResponseEntity<ImageUploadAckResponse> createImageMetadata(
            Authentication authentication,
            @RequestBody ImageMetadataRequest request
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        ImageUploadAckResponse res =
                imageServicePort.createImageMetadata(userId, projectId, request);

        return ResponseEntity.ok(res);
    }


    @Deprecated
    @PutMapping(value = "/file", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Void> uploadImageFileStream(
            Authentication authentication,
            @RequestParam("imageId") String imageId,
            HttpServletRequest request
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        try {
            InputStream bodyStream = request.getInputStream();
            imageServicePort.uploadFileStream(
                    userId, projectId, imageId, bodyStream, MediaType.APPLICATION_OCTET_STREAM_VALUE
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_FILE_UPLOAD_FAILURE, e);
        }
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteImage(
            Authentication authentication,
            @RequestParam("imageId") String imageId
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        imageServicePort.deleteImage(userId, projectId, imageId);

        return ResponseEntity.ok().build();
    }
}
