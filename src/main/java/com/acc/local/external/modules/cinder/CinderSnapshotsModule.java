package com.acc.local.external.modules.cinder;

import com.acc.local.external.dto.cinder.snapshot.*;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CinderSnapshotsModule extends CinderAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listSnapshotsDetail(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v3/" + projectId + "/snapshots/detail";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createSnapshot(String token, String projectId, CreateSnapshotRequest request) {
        String uri = "/v3/" + projectId + "/snapshots";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> listSnapshots(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v3/" + projectId + "/snapshots";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> getSnapshotMetadata(String token, String projectId, String snapshotId) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> createSnapshotMetadata(String token, String projectId, String snapshotId, CreateSnapshotMetadataRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> getSnapshot(String token, String projectId, String snapshotId) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateSnapshotMetadata(String token, String projectId, String snapshotId, UpdateSnapshotMetadataRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> updateSnapshot(String token, String projectId, String snapshotId, UpdateSnapshotRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> getSnapshotMetadataKey(String token, String projectId, String snapshotId, String key) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata/" + key;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateSnapshotMetadataKey(String token, String projectId, String snapshotId, String key, UpdateSnapshotMetadataKeyRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata/" + key;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteSnapshot(String token, String projectId, String snapshotId) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> deleteSnapshotMetadataKey(String token, String projectId, String snapshotId, String key) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata/" + key;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> resetSnapshotStatus(String token, String projectId, String snapshotId, ResetSnapshotStatusRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> updateSnapshotStatus(String token, String projectId, String snapshotId, UpdateSnapshotStatusRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> forceDeleteSnapshot(String token, String projectId, String snapshotId, ForceDeleteSnapshotRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }
}
