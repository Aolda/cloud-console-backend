package com.acc.local.controller.docs;

import com.acc.global.common.ApiResponse;
import com.acc.global.common.PageRequest;
import com.acc.local.dto.image.*;
import com.acc.local.dto.image.ImageListResponse.GlanceImageSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Images", description = "Glance 이미지 관리 API")
@RequestMapping("/api/v1/images")
public interface ImageDocs {

    @Operation(
            summary = "이미지 목록/상세 조회",
            description = "image_id 존재 시 상세 조회, 없으면 페이징 목록 조회",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer {access_token}",
                            required = true,
                            in = ParameterIn.HEADER
                    )
            }
    )
    @GetMapping
    ResponseEntity<ApiResponse<?>> getImages(
            Authentication authentication,
            @RequestParam(value = "image_id", required = false) String imageId,
            @RequestParam(value = "scope", required = false) String scope,
            @ModelAttribute PageRequest pageRequest
    );


    @Operation(
            summary = "이미지 URL Import",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer {access_token}",
                            required = true,
                            in = ParameterIn.HEADER
                    )
            }
    )
    @PostMapping("/import")
    ResponseEntity<ApiResponse<ImageUploadAckResponse>> importImageByUrl(
            Authentication authentication,
            @RequestBody ImageUrlImportRequest request
    );


    @Operation(
            summary = "이미지 메타데이터 생성",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer {access_token}",
                            required = true,
                            in = ParameterIn.HEADER
                    )
            }
    )
    @PostMapping("/metadata")
    ResponseEntity<ApiResponse<ImageUploadAckResponse>> createImageMetadata(
            Authentication authentication,
            @RequestBody ImageMetadataRequest request
    );


    @Operation(
            summary = "이미지 파일 스트림 업로드",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer {access_token}",
                            required = true,
                            in = ParameterIn.HEADER
                    )
            }
    )
    @PutMapping(value = "/file", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<ApiResponse<Void>> uploadImageFileStream(
            Authentication authentication,
            @RequestParam("image_id") String imageId,
            HttpServletRequest request
    );


    @Operation(
            summary = "이미지 삭제",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer {access_token}",
                            required = true,
                            in = ParameterIn.HEADER
                    )
            }
    )
    @DeleteMapping
    ResponseEntity<ApiResponse<Void>> deleteImage(
            Authentication authentication,
            @RequestParam("image_id") String imageId
    );
}
