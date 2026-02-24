package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.global.exception.image.ImageException;
import com.acc.global.security.session.SessionPrincipal;
import com.acc.local.controller.docs.ImageDocs;
import com.acc.local.dto.image.*;
import com.acc.local.service.ports.ImageServicePort;
import io.swagger.v3.oas.annotations.Parameter;
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
    public ResponseEntity<PageResponse<GlanceImageSummary>> getImages(
            @ModelAttribute PageRequest pageRequest,
            @ModelAttribute ImageFilterRequest filterRequest,
            Authentication authentication,
            @RequestParam String projectId
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        // marker 단독 금지
        if (pageRequest.getMarker() != null && pageRequest.getLimit() == null) {
            throw new ImageException(ImageErrorCode.INVALID_PAGINATION_PARAM);
        }
        // direction 단독 금지
        if (pageRequest.getDirection() != null && pageRequest.getLimit() == null) {
            throw new ImageException(ImageErrorCode.INVALID_PAGINATION_PARAM);
        }

        PageResponse<GlanceImageSummary> page = imageServicePort.getImagesWithPagination(sessionId, projectId, pageRequest, filterRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<ImageDetailResponse> getImageDetail(
            Authentication authentication,
            @RequestParam String projectId,
            @PathVariable String imageId
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        ImageDetailResponse detail = imageServicePort.getImageDetail(sessionId, projectId, imageId);
        return ResponseEntity.ok(detail);
    }

    @PostMapping("/import")
    public ResponseEntity<ImageUploadAckResponse> importImageByUrl(
            Authentication authentication,
            @RequestBody ImageUrlImportRequest request,
            @RequestParam String projectId
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        ImageUploadAckResponse res =
                imageServicePort.importImageByUrl(sessionId, projectId, request);

        return ResponseEntity.ok(res);
    }


    @Deprecated
    @PostMapping("/metadata")
    public ResponseEntity<ImageUploadAckResponse> createImageMetadata(
            Authentication authentication,
            @RequestBody ImageMetadataRequest request,
            @RequestParam String projectId
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        ImageUploadAckResponse res =
                imageServicePort.createImageMetadata(sessionId, projectId, request);

        return ResponseEntity.ok(res);
    }


    @Deprecated
    @PutMapping(value = "/file", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Void> uploadImageFileStream(
            Authentication authentication,
            @RequestParam("imageId") String imageId,
            HttpServletRequest request,
            @RequestParam String projectId
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        try {
            InputStream bodyStream = request.getInputStream();
            imageServicePort.uploadFileStream(
                    sessionId, projectId, imageId, bodyStream, MediaType.APPLICATION_OCTET_STREAM_VALUE
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_UPLOAD_FAILURE, e);
        }
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteImage(
            Authentication authentication,
            @RequestParam("imageId") String imageId,
            @RequestParam String projectId
    ) {
        SessionPrincipal principal = (SessionPrincipal) authentication.getPrincipal();
        String sessionId = principal.getSessionId();

        imageServicePort.deleteImage(sessionId, projectId, imageId);

        return ResponseEntity.ok().build();
    }
}
