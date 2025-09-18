package com.acc.local.external.modules.nova;

import com.acc.local.external.dto.nova.volumeAttachment.AttachVolumeRequest;
import com.acc.local.external.dto.nova.volumeAttachment.UpdateVolumeAttachmentsRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class NovaServerVolumeAttachmentAPIModule extends NovaAPIUtil
{

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> ListVolumeAttachments(String token, String serverId) {
        String uri = "/v2.1/servers/" + serverId + "/os-volume_attachments";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> attachVolume(String token, String serverId, AttachVolumeRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/os-volume_attachments";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showVolumeAttachmentsDetail(String token, String serverId, String volumeId) {
        String uri = "/v2.1/servers/" + serverId + "/os-volume_attachments/" + volumeId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateVolumeAttachments(String token, String serverId, String volumeId, UpdateVolumeAttachmentsRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/os-volume_attachments/" + volumeId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> detachVolume(String token, String serverId, String volumeId) {
        String uri = "/v2.1/servers/" + serverId + "/os-volume_attachments/" + volumeId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}
