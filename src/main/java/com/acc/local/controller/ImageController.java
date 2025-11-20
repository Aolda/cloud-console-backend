package com.acc.local.controller;

import com.acc.global.security.jwt.JwtInfo;
import com.acc.local.dto.image.*;
import com.acc.local.service.ports.ImageServicePort;
import com.acc.global.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(value = "image_id", required = false) String imageId
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        if (imageId != null) {
            ImageDetailResponse detail =
                    imageServicePort.getImageDetail(userId, projectId, imageId);

            return ResponseEntity.ok(
                    ApiResponse.success("이미지 상세 조회 성공", detail)
            );
        }

        ImageListResponse list =
                imageServicePort.getPrivateImages(userId, projectId);

        return ResponseEntity.ok(
                ApiResponse.success("이미지 목록 조회 성공", list)
        );
    }


    @PostMapping("/import")
    public ResponseEntity<ApiResponse<ImageUploadAckResponse>> importImageByUrl(
            Authentication authentication,
            @RequestBody ImageUrlImportRequest request
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        ImageUploadAckResponse res =
                imageServicePort.importImageByUrl(userId, projectId, request);

        return ResponseEntity.ok(
                ApiResponse.success("이미지 URL Import 요청 성공", res)
        );
    }

    @PostMapping("/metadata")
    public ResponseEntity<ApiResponse<ImageUploadAckResponse>> createImageMetadata(
            Authentication authentication,
            @RequestBody ImageMetadataRequest request
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        ImageUploadAckResponse res =
                imageServicePort.createImageMetadata(userId, projectId, request);

        return ResponseEntity.ok(
                ApiResponse.success("이미지 메타데이터 생성 성공", res)
        );
    }

    @PostMapping(value = "/{imageId}/file", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ApiResponse<Void>> uploadImageFileStream(
            Authentication authentication,
            @PathVariable String imageId,
            HttpServletRequest request
    ) throws IOException {

        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        String contentType = request.getContentType();
        InputStream bodyStream = request.getInputStream(); // 핵심: 프록시 스트림

        imageServicePort.uploadFileStream(
                userId,
                projectId,
                imageId,
                bodyStream,
                contentType
        );

        return ResponseEntity.ok(
                ApiResponse.success("이미지 파일 스트림 업로드 성공")
        );
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            Authentication authentication,
            @PathVariable String imageId
    ) {
        JwtInfo jwtInfo = (JwtInfo) authentication.getPrincipal();
        String userId = jwtInfo.getUserId();
        String projectId = jwtInfo.getProjectId();

        imageServicePort.deleteImage(userId, projectId, imageId);

        return ResponseEntity.ok(
                ApiResponse.success("이미지 삭제 성공")
        );
    }
}
