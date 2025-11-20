package com.acc.local.controller;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.global.exception.image.ImageException;
import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.dto.image.*;
import com.acc.local.dto.image.ImageListResponse.GlanceImageSummary;
import com.acc.local.service.ports.ImageServicePort;
import com.acc.global.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageServicePort imageServicePort;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getImages(
            Authentication authentication,
            @RequestParam(value = "image_id", required = false) String imageId,
            @RequestParam(value = "scope", required = false) String scope,
            PageRequest pageRequest
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
        if (imageId != null) {
            ImageDetailResponse detail = imageServicePort.getImageDetail(userId, projectId, imageId);
            return ResponseEntity.ok(ApiResponse.success("이미지 상세 조회 성공", detail));
        }
        PageResponse<GlanceImageSummary> page = imageServicePort.getImagesWithPagination(userId, projectId, pageRequest);
        return ResponseEntity.ok(ApiResponse.success("이미지 목록 조회 성공", page));
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<ImageUploadAckResponse>> importImageByUrl(
            Authentication authentication,
            @RequestBody ImageUrlImportRequest request
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        ImageUploadAckResponse res = imageServicePort.importImageByUrl(userId, projectId, request);

        return ResponseEntity.ok(ApiResponse.success("이미지 URL Import 요청 성공", res));
    }

    @PostMapping("/metadata")
    public ResponseEntity<ApiResponse<ImageUploadAckResponse>> createImageMetadata(
            Authentication authentication,
            @RequestBody ImageMetadataRequest request
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
        ImageUploadAckResponse res = imageServicePort.createImageMetadata(userId, projectId, request);
        return ResponseEntity.ok(ApiResponse.success("이미지 메타데이터 생성 성공", res));
    }

    @PutMapping(value = "/file", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ApiResponse<Void>> uploadImageFileStream(Authentication authentication, @RequestParam("image_id") String imageId, HttpServletRequest request) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
        try {
            String contentType = request.getContentType();
            InputStream bodyStream = request.getInputStream();
            imageServicePort.uploadFileStream(userId, projectId, imageId, bodyStream, contentType);
            return ResponseEntity.ok(ApiResponse.success("이미지 파일 스트림 업로드 성공"));
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_FILE_UPLOAD_FAILURE, e);
        }
    }


    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            Authentication authentication,
            @RequestParam("image_id") String imageId
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();
        imageServicePort.deleteImage(userId, projectId, imageId);
        return ResponseEntity.ok(ApiResponse.success("이미지 삭제 성공"));
    }
}
