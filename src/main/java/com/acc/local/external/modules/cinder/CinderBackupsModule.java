package com.acc.local.external.modules.cinder;

import com.acc.local.external.dto.cinder.backup.*;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CinderBackupsModule extends CinderAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listBackupsDetail(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v3/backups/detail";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> restoreBackup(String token, String projectId, String backupId, RestoreBackupRequest request) {
        String uri = "/v3/backups/" + backupId + "/restore";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> createBackup(String token, String projectId, CreateBackupRequest request) {
        String uri = "/v3/backups";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> getBackup(String token, String projectId, String backupId) {
        String uri = "/v3/backups/" + backupId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateBackup(String token, String projectId, String backupId, UpdateBackupRequest request) {
        String uri = "/v3/backups/" + backupId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> importBackup(String token, String projectId, ImportBackupRequest request) {
        String uri = "/v3/backups/import_record";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> listBackups(String token, String projectId, Map<String, String> queryParams) {
        String uri = "/v3/backups";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> exportBackupRecord(String token, String projectId, String backupId) {
        String uri = "/v3/backups/" + backupId + "/export_record";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> deleteBackup(String token, String projectId, String backupId) {
        String uri = "/v3/backups/" + backupId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> forceDeleteBackup(String token, String projectId, String backupId, ForceDeleteBackupRequest request) {
        String uri = "/v3/backups/" + backupId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> resetBackupStatus(String token, String projectId, String backupId, ResetBackupStatusRequest request) {
        String uri = "/v3/backups/" + backupId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }
}
