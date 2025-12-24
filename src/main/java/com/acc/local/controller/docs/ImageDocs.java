package com.acc.local.controller.docs;

import com.acc.global.common.PageRequest;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.local.dto.image.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Images", description = "Glance 이미지 관리 API")
@RequestMapping("/api/v1/images")
@SecurityRequirement(name = "access-token")
public interface ImageDocs {

    // ------------------------------------------------------------------------
    // GET /images (fetch list or detail)
    // ------------------------------------------------------------------------
    @Operation(
            summary = "이미지 목록/상세 조회",
            description = """
                imageId 존재 시 상세 조회,
                없으면 필터 + 페이지네이션 기반 목록 조회.

                ❗ pagination 규칙
                - marker 단독 사용 금지
                - direction 단독 사용 금지
                - imageId 와 pagination 파라미터 동시 금지
                """
    )
    @ApiResponses({
            // ----- 성공 응답 -----
            @ApiResponse(
                    responseCode = "200",
                    description = "단건 조회 또는 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Object.class),
                            examples = {
                                    // -------- 목록 예시 --------
                                    @ExampleObject(
                                            name = "ListResponse Example",
                                            summary = "[목록 조회 예시]",
                                            value = """
                                            {
                                              "contents": [
                                                {
                                                  "id": "5f83ac1f-0d40-4e74-8088-c9eb5cfdb190",
                                                  "name": "ubuntu-22.04-test",
                                                  "architecture": null,
                                                  "projectName": null,
                                                  "description": null,
                                                  "diskFormat": "qcow2",
                                                  "status": "active",
                                                  "visibility": "private",
                                                  "size": 691018752,
                                                  "hidden": null,
                                                  "minDisk": 20,
                                                  "minRam": 2048,
                                                  "createdAt": "2025-11-21T07:52:24Z"
                                                }
                                              ],
                                              "first": true,
                                              "last": false,
                                              "size": 1,
                                              "nextMarker": "5f83ac1f-0d40-4e74-8088-c9eb5cfdb190"
                                            }
                                            """
                                    ),
                                    // -------- 상세 예시 --------
                                    @ExampleObject(
                                            name = "DetailResponse Example",
                                            summary = "[단건 조회 예시]",
                                            value = """
                                            {
                                              "id": "4c64f22d-f832-42f2-ae83-8767937cfcf2",
                                              "name": "ubuntu-24.04-noble",
                                              "architecture": "x86_64",
                                              "projectName": null,
                                              "description": null,
                                              "diskFormat": "qcow2",
                                              "containerFormat": "bare",
                                              "visibility": "public",
                                              "status": "active",
                                              "size": 625911296,
                                              "virtualSize": 3758096384,
                                              "minDisk": 0,
                                              "minRam": 0,
                                              "checksum": "6ff9c27a2d598d4f577801e318cf7806",
                                              "osHashAlgo": "sha512",
                                              "osHashValue": "27506be476...",
                                              "osVersion": "24.04",
                                              "osAdminUser": null,
                                              "deleteProtected": false,
                                              "hidden": false,
                                              "tags": ["linux","cloud","noble","ubuntu"],
                                              "stores": "file",
                                              "createdAt": "2025-11-23T18:08:55Z",
                                              "updatedAt": "2025-11-23T18:54:53Z"
                                            }
                                            """
                                    )
                            }
                    )
            ),

            // ----- 에러 응답 -----
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(
                            schema = @Schema(implementation = ImageErrorCode.class),
                            examples = {
                                    @ExampleObject(name="INVALID_PAGINATION_PARAM", value="{\"code\":\"ACC-IMAGE-PAGINATION-PARAM-INVALID\",\"message\":\"잘못된 페이지네이션 파라미터 조합입니다.\"}"),
                                    @ExampleObject(name="INVALID_PAGINATION_WITH_IMAGE_ID", value="{\"code\":\"ACC-IMAGE-PAGINATION-WITH-IMAGE-ID\",\"message\":\"단건 조회 시 페이지네이션 파라미터를 사용할 수 없습니다.\"}"),
                            }
                    )
            ),

            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ImageErrorCode.class),
                            examples = {
                                    @ExampleObject(name="IMAGE_NOT_ACCESSIBLE", value="{\"code\":\"ACC-IMAGE-NO-PERMISSION\",\"message\":\"해당 이미지에 접근할 권한이 없습니다.\"}")
                            }
                    )
            ),
            @ApiResponse(responseCode="404", description="이미지 없음",
                    content=@Content(
                            schema=@Schema(implementation = ImageErrorCode.class),
                            examples=@ExampleObject(name="IMAGE_NOT_FOUND", value="{\"code\":\"ACC-IMAGE-NOT-FOUND\",\"message\":\"요청한 이미지를 찾을 수 없습니다.\"}")
                    )
            ),
            @ApiResponse(responseCode="502", description="Glance 오류",
                    content=@Content(
                            schema=@Schema(implementation = ImageErrorCode.class),
                            examples={
                                    @ExampleObject(name="GLANCE_UNAVAILABLE", value="{\"code\":\"ACC-GLANCE-UNAVAILABLE\",\"message\":\"Glance 서비스와 통신할 수 없습니다.\"}"),
                                    @ExampleObject(name="GLANCE_BAD_RESPONSE", value="{\"code\":\"ACC-GLANCE-BAD-RESPONSE\",\"message\":\"Glance로부터 비정상 응답이 수신되었습니다.\"}")
                            }
                    )
            )
    })
    @GetMapping
    ResponseEntity<?> getImages(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam(value = "imageId", required = false) String imageId,
            @ParameterObject @ModelAttribute PageRequest pageRequest,
            @ParameterObject @ModelAttribute ImageFilterRequest filterRequest
    );


    // ------------------------------------------------------------------------
    // POST /images/import
    // ------------------------------------------------------------------------
    @Operation(
            summary = "이미지 URL Import",
            description = """
        Glance 이미지에 대한 URL 기반 Import 요청.
        1) 메타데이터 생성
        2) URL import 실행
        3) 실패 시 rollback(delete)
        """
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "Import 요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImageUploadAckResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "클라이언트 잘못된 입력",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImageErrorCode.class),
                            examples = {
                                    @ExampleObject(
                                            name = "INVALID_IMPORT_URL",
                                            value = "{\"code\":\"ACC-IMAGE-IMPORT-URL-INVALID\",\"message\":\"이미지 URL 형식이 올바르지 않습니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "INVALID_IMAGE_METADATA",
                                            value = "{\"code\":\"ACC-IMAGE-METADATA-INVALID\",\"message\":\"이미지 메타데이터 형식이 올바르지 않습니다.\"}"
                                    )
                            }
                    )
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "메타데이터 생성 금지",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "IMAGE_METADATA_CREATE_FORBIDDEN",
                                    value = "{\"code\":\"ACC-IMAGE-METADATA-CREATE-FORBIDDEN\",\"message\":\"이미지 메타데이터 생성 권한이 없습니다.\"}"
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Import 중 내부 오류(Glance 403/409 등 승격)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "IMAGE_IMPORT_FAILURE",
                                    value = "{\"code\":\"ACC-IMAGE-IMPORT-FAILURE\",\"message\":\"이미지 import 중 오류가 발생하였습니다.\"}"
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "502",
                    description = "Glance 다운/장애",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "GLANCE_UNAVAILABLE",
                                            value = "{\"code\":\"ACC-GLANCE-UNAVAILABLE\",\"message\":\"Glance 서비스와 통신할 수 없습니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "GLANCE_BAD_RESPONSE",
                                            value = "{\"code\":\"ACC-GLANCE-BAD-RESPONSE\",\"message\":\"Glance로부터 비정상 응답이 수신되었습니다.\"}"
                                    )
                            }
                    )
            )
    })
    @PostMapping("/import")
    ResponseEntity<ImageUploadAckResponse> importImageByUrl(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody ImageUrlImportRequest request
    );


    // ------------------------------------------------------------------------
    // POST /images/metadata (Deprecated)
    // ------------------------------------------------------------------------
    @Operation(
            summary = "[Deprecated] 이미지 메타데이터 생성 API",
            description = """
                Put File 비활성화로 필요 없는 기능의 엔드포인트 입니다.
                URL로 업로드는 "/import" 엔드포인트에서 동작합니다.
                참고: MR !67
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode="200", description="메타데이터 생성 성공"),
            @ApiResponse(responseCode="400", description="INVALID_IMAGE_METADATA")
    })
    @Deprecated
    @PostMapping("/metadata")
    ResponseEntity<ImageUploadAckResponse> createImageMetadata(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody ImageMetadataRequest request
    );


    // ------------------------------------------------------------------------
    // PUT /images/file (Deprecated)
    // ------------------------------------------------------------------------
    @Operation(
            summary = "[Deprecated] 이미지 파일 업로드 API",
            description = """
                디스크 I/O 병목 이슈로 현재 비활성화된 엔드포인트입니다.
                추후 안정화 시 재도입 예정.
                참고: MR !67
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode="200", description="파일 업로드 성공"),
            @ApiResponse(responseCode="500", description="IMAGE_UPLOAD_FAILURE")
    })
    @Deprecated
    @PutMapping(value = "/file", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<Void> uploadImageFileStream(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam("imageId") String imageId,
            HttpServletRequest request
    );


    // ------------------------------------------------------------------------
    // DELETE /images
    // ------------------------------------------------------------------------
    @Operation(
            summary = "이미지 삭제",
            description = "Glance 이미지 삭제 요청.\n권한 부족/없는 이미지/Glance 장애 등에 따라 다양한 오류가 발생할 수 있음.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer {access_token}",
                            required = true,
                            in = ParameterIn.HEADER
                    )
            }
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공"
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "삭제 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "IMAGE_DELETE_FORBIDDEN",
                                    value = "{\"code\":\"ACC-IMAGE-DELETE-FORBIDDEN\",\"message\":\"이미지를 삭제할 권한이 없습니다.\"}"
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "404",
                    description = "이미지 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "IMAGE_NOT_FOUND",
                                    value = "{\"code\":\"ACC-IMAGE-NOT-FOUND\",\"message\":\"요청한 이미지를 찾을 수 없습니다.\"}"
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "502",
                    description = "Glance 문제로 삭제 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "IMAGE_STATUS_CONFLICT",
                                            value = "{\"code\":\"ACC-IMAGE-STATUS-CONFLICT\",\"message\":\"현재 상태에서는 요청을 처리할 수 없습니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "GLANCE_UNAVAILABLE",
                                            value = "{\"code\":\"ACC-GLANCE-UNAVAILABLE\",\"message\":\"Glance 서비스와 통신할 수 없습니다.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "GLANCE_BAD_RESPONSE",
                                            value = "{\"code\":\"ACC-GLANCE-BAD-RESPONSE\",\"message\":\"Glance로부터 비정상 응답이 수신되었습니다.\"}"
                                    )
                            }
                    )
            )
    })
    @DeleteMapping
    ResponseEntity<Void> deleteImage(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam("imageId") String imageId
    );

}
