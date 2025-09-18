package com.acc.local.external.modules.cinder;

import com.acc.local.external.dto.cinder.attachment.CompleteAttachmentRequest;
import com.acc.local.external.dto.cinder.attachment.CreateAttachmentRequest;
import com.acc.local.external.dto.cinder.attachment.UpdateAttachmentRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CinderAttachmentsModule extends CinderAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> getAttachment(String token, String projectId, String attachmentId) {
        String uri = "/v3/" + projectId + "/attachments/" + attachmentId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> createAttachment(String token, String projectId, CreateAttachmentRequest request) {
        String uri = "/v3/" + projectId + "/attachments";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> listAttachmentsDetail(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v3/" + projectId + "/attachments/detail";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> listAttachments(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v3/" + projectId + "/attachments";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> deleteAttachment(String token, String projectId, String attachmentId) {
        String uri = "/v3/" + projectId + "/attachments/" + attachmentId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> updateAttachment(String token, String projectId, String attachmentId, UpdateAttachmentRequest request) {
        String uri = "/v3/" + projectId + "/attachments/" + attachmentId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> completeAttachment(String token, String projectId, String attachmentId, CompleteAttachmentRequest request) {
        String uri = "/v3/" + projectId + "/attachments/" + attachmentId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }
}
