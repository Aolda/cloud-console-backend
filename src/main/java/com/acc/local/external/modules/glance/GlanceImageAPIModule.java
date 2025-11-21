package com.acc.local.external.modules.glance;

import com.acc.local.external.dto.glance.image.*;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collections;
import java.util.Vector;

@Component
@RequiredArgsConstructor
public class GlanceImageAPIModule {

    private final OpenstackAPICallModule openstackAPICallModule;
    private final int port = 9292;

    // Show image
    public ResponseEntity<JsonNode> fetchImage(String token, String imageId) {
        String uri = "/v2/images/" + imageId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    // List images
    public ResponseEntity<JsonNode> fetchImageList(String token, FetchImagesRequestParam params) {
        String uri = "/v2/images";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), params.toQueryParams(), port);
    }

    // Show tasks associated with image
    public ResponseEntity<JsonNode> fetchImageTask(String token, String imageId) {
        String uri = "/v2/images/" + imageId + "/tasks";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    // Create Image (메타데이터 생성)
    public ResponseEntity<JsonNode> createImage(String token, CreateImageRequest request) {
        String uri = "/v2/images";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    // Stage Image (이미지 업로드 준비 단계)
    public ResponseEntity<JsonNode> stageImage(String token, String imageId, StageImageRequest request) {
        String uri = "/v2/images/" + imageId + "/stage";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    // Import Image (외부 URL로 import)
    public ResponseEntity<Void> importImage(String token, String imageId, ImportImageRequest request) {
        String uri = "/v2/images/" + imageId + "/import";
        return openstackAPICallModule.callPostAPINoBody(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    // Update Image (메타데이터 수정)
    public ResponseEntity<JsonNode> updateImage(String token, String imageId, UpdateImageRequest request) {
        String uri = "/v2/images/" + imageId;
        return openstackAPICallModule.callPatchAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    // Delete Image
    public ResponseEntity<JsonNode> deleteImage(String token, String imageId) {
        String uri = "/v2/images/" + imageId;
        return openstackAPICallModule.callDeleteAPI(
                uri,
                Collections.singletonMap("X-Auth-Token", token),
                port
        );
    }

    //file Upload
    public ResponseEntity<JsonNode> uploadImageFileStream(String token, String imageId, InputStreamResource resource, String contentType) {
        String uri = "/v2/images/" + imageId + "/file";
        return openstackAPICallModule.callPutBinaryStreamAPI(
                uri,
                Collections.singletonMap("X-Auth-Token", token),
                resource,
                contentType,
                port
        );
    }
}
