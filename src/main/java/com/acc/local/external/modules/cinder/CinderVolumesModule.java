package com.acc.local.external.modules.cinder;

import com.acc.local.external.dto.cinder.volume.*;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CinderVolumesModule extends CinderAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listVolumesDetail(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v3/" + projectId + "/volumes/detail";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createVolume(String token, String projectId, CreateVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> listVolumes(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v3/" + projectId + "/volumes";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> getVolume(String token, String projectId, String volumeId) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateVolume(String token, String projectId, String volumeId, UpdateVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteVolume(String token, String projectId, String volumeId) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> createVolumeMetadata(String token, String projectId, String volumeId, VolumeMetadataRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> getVolumeMetadata(String token, String projectId, String volumeId) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateVolumeMetadata(String token, String projectId, String volumeId, VolumeMetadataRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> getVolumeMetadataKey(String token, String projectId, String volumeId, String key) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata/" + key;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> deleteVolumeMetadataKey(String token, String projectId, String volumeId, String key) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata/" + key;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> updateVolumeMetadataKey(String token, String projectId, String volumeId, String key, UpdateVolumeMetadataKeyRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata/" + key;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> getVolumesSummary(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v3/" + projectId + "/volumes/summary";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    // ============ Actions ============
    public ResponseEntity<JsonNode> extendVolume(String token, String projectId, String volumeId, ExtendVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> completeExtendVolume(String token, String projectId, String volumeId, ExtendVolumeCompletionRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> resetVolumeStatus(String token, String projectId, String volumeId, ResetVolumeStatusRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> revertVolumeToSnapshot(String token, String projectId, String volumeId, RevertVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> setImageMetadata(String token, String projectId, String volumeId, SetImageMetadataRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> unsetImageMetadata(String token, String projectId, String volumeId, UnsetImageMetadataRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showImageMetadata(String token, String projectId, String volumeId, ShowImageMetadataRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> attachVolume(String token, String projectId, String volumeId, AttachVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> detachVolume(String token, String projectId, String volumeId, DetachVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> unmanageVolume(String token, String projectId, String volumeId, UnmanageVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> forceDetachVolume(String token, String projectId, String volumeId, ForceDetachVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> retypeVolume(String token, String projectId, String volumeId, RetypeVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> migrateVolume(String token, String projectId, String volumeId, MigrateVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> completeMigrateVolume(String token, String projectId, String volumeId, CompleteMigrateVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> forceDeleteVolume(String token, String projectId, String volumeId, ForceDeleteVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> setBootableVolume(String token, String projectId, String volumeId, SetBootableVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> uploadVolumeToImage(String token, String projectId, String volumeId, UploadVolumeToImageRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> reserveVolume(String token, String projectId, String volumeId, ReserveVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> unreserveVolume(String token, String projectId, String volumeId, UnreserveVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> beginDetachingVolume(String token, String projectId, String volumeId, BeginDetachingVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> rollDetachingVolume(String token, String projectId, String volumeId, RollDetachingVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> terminateVolumeConnection(String token, String projectId, String volumeId, TerminateConnectionRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> initializeVolumeConnection(String token, String projectId, String volumeId, InitializeConnectionRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> updateVolumeReadonlyFlag(String token, String projectId, String volumeId, UpdateReadonlyFlagRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> reimageVolume(String token, String projectId, String volumeId, ReimageVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }
}
