package com.acc.local.external.modules.nova;

import com.acc.local.external.dto.nova.serverAction.*;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NovaServerActionAPIModule extends NovaAPIUtil
{
    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> addSecurityGroupToServer(String token, String serverId, String securityGroupName) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        Map<String, Object> request = Collections.singletonMap("addSecurityGroup", Collections.singletonMap("name", securityGroupName));
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> changeAdminPassword(String token, String serverId, String adminPassword) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        Map<String, Object> request = Collections.singletonMap("changePassword", Collections.singletonMap("adminPass", adminPassword));
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> confirmResizeServer(String token, String serverId, ConfirmResizeRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> createServerBackup(String token, String serverId, CreateBackupRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> createImage(String token, String serverId, CreateImageRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> lockServer(String token, String serverId, LockServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> pauseServer(String token, String serverId, PauseServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> rebootServer(String token, String serverId, String rebootType) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        Map<String, Object> request = Collections.singletonMap("reboot", Collections.singletonMap("type", rebootType));
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> rebuildServer(String token, String serverId, RebuildServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> removeSecurityGroupFromServer(String token, String serverId, String securityGroupName) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        Map<String, Object> request = Collections.singletonMap("removeSecurityGroup", Collections.singletonMap("name", securityGroupName));
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> rescueServer(String token, String serverId, RescueServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> resizeServer(String token, String serverId, ResizeServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> resumeServer(String token, String serverId, ResumeServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> revertResize(String token, String serverId, RevertResizeRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> startServer(String token, String serverId, StartServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> stopServer(String token, String serverId, StopServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> suspendServer(String token, String serverId, SuspendServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> unlockServer(String token, String serverId, UnlockServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> unpauseServer(String token, String serverId, UnpauseServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> unrescueServer(String token, String serverId, UnrescueServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> forceDeleteServer(String token, String serverId, ForceDeleteServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> restoreServer(String token, String serverId, RestoreServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> shelveServer(String token, String serverId, ShelveServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> shelveOffloadServer(String token, String serverId, ShelveOffloadServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> unshelveServer(String token, String serverId, UnshelveServerRequest request) {
        String uri = "/v2.1/servers/" + serverId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }


}
