package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.local.dto.image.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Images", description = "Glance 이미지 관리 API")
@RequestMapping("/api/v1/images")
public interface ImageDocs {

    @Operation(
            summary = "이미지 목록/상세 조회",
            description = """
                imageId 존재 시 상세 조회,
                없으면 필터 + 페이지네이션 기반 목록 조회.
                """
    )
    @GetMapping
    ResponseEntity<?> getImages(
            Authentication authentication,
            @RequestParam(value = "imageId", required = false) String imageId,
            @ParameterObject @ModelAttribute PageRequest pageRequest,
            @ParameterObject @ModelAttribute ImageFilterRequest filterRequest
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
    ResponseEntity<ImageUploadAckResponse> importImageByUrl(
            Authentication authentication,
            @RequestBody ImageUrlImportRequest request
    );


    @Operation(
            summary = "[Deprecated] 이미지 메타데이터 생성 API",
            description = """
                Put File 비활성화로 필요 없는 기능의 엔드포인트 입니다.
                URL로 업로드는 "/import" 엔드포인트에서 동작합니다.
                참고: MR !67
            """
    )
    @Deprecated
    @PostMapping("/metadata")
    ResponseEntity<ImageUploadAckResponse> createImageMetadata(
            Authentication authentication,
            @RequestBody ImageMetadataRequest request
    );


    @Operation(
            summary = "[Deprecated] 이미지 파일 업로드 API",
            description = """
                디스크 I/O 병목 이슈로 현재 비활성화된 엔드포인트입니다.
                추후 안정화 시 재도입 예정.
                참고: MR !67
            """
    )
    @Deprecated
    @PutMapping(value = "/file", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<Void> uploadImageFileStream(
            Authentication authentication,
            @RequestParam("imageId") String imageId,
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
    ResponseEntity<Void> deleteImage(
            Authentication authentication,
            @RequestParam("imageId") String imageId
    );
}
