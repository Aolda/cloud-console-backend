package com.acc.local.external.modules;

import com.acc.local.external.dto.nova.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NovaAPIModule {
    //TODO: 추후 병합 시 clout-console-backend 리포지토리 로직으로 merge
//    public final OpenstackAPICallModule openstackAPICallModule;
//
//    public JsonNode addSecurityGroupToServer(String token, String serverId, AddSecurityGroupRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    public JsonNode changeAdminPassword(String token, String serverId, ChangePasswordRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    public JsonNode confirmResizeServer(String token, String serverId, ConfirmResizeRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    public JsonNode createServerBackup(String token, String serverId, CreateBackupRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Create Image from Server
//    public JsonNode createImage(String token, String serverId, CreateImageRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Remove Security Group from Server
//    public JsonNode removeSecurityGroupFromServer(String token, String serverId, RemoveSecurityGroupRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Reboot Server
//    public JsonNode rebootServer(String token, String serverId, RebootServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Resize Server
//    public JsonNode resizeServer(String token, String serverId, ResizeServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Revert Resize
//    public JsonNode revertResize(String token, String serverId, RevertResizeRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Start Server
//    public JsonNode startServer(String token, String serverId, StartServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Stop Server
//    public JsonNode stopServer(String token, String serverId, StopServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Suspend Server
//    public JsonNode suspendServer(String token, String serverId, SuspendServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Resume Server
//    public JsonNode resumeServer(String token, String serverId, ResumeServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Pause Server
//    public JsonNode pauseServer(String token, String serverId, PauseServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Unpause Server
//    public JsonNode unpauseServer(String token, String serverId, UnpauseServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Lock Server
//    public JsonNode lockServer(String token, String serverId, LockServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Unlock Server
//    public JsonNode unlockServer(String token, String serverId, UnlockServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Attach Volume
//    public JsonNode attachVolume(String token, String serverId, AttachVolumeRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/os-volume_attachments";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Detach Volume
//    public JsonNode detachVolume(String token, String serverId, String volumeId) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/os-volume_attachments/" + volumeId;
//        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token));
//    }
//
//    // Update Server
//    public JsonNode updateServer(String token, String serverId, UpdateServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId;
//        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Delete Server
//    public JsonNode deleteServer(String token, String serverId) {
//        String uri = "/compute/v2.1/servers/" + serverId;
//        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token));
//    }
//
//    // Get Server Details
//    public JsonNode getServerDetails(String token, String serverId) {
//        String uri = "/compute/v2.1/servers/" + serverId;
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // List Servers
//    public JsonNode listServers(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/servers";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    // List Servers without query parameters
//    public JsonNode listServers(String token) {
//        return listServers(token, Collections.emptyMap());
//    }
//
//    // List Servers with Details (same as listServers but with more detailed response)
//    public JsonNode listServersDetail(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/servers/detail";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    // List Servers with Details without query parameters
//    public JsonNode listServersDetail(String token) {
//        return listServersDetail(token, Collections.emptyMap());
//    }
//
//    // Get Server Metadata
//    public JsonNode getServerMetadata(String token, String serverId) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/metadata";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // Update Server Metadata
//    public JsonNode updateServerMetadata(String token, String serverId, Map<String, String> metadata) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/metadata";
//        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.singletonMap("metadata", metadata));
//    }
//
//    // Delete Server Metadata Item
//    public JsonNode deleteServerMetadataItem(String token, String serverId, String key) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/metadata/" + key;
//        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token));
//    }
//
//    // Get Server Metadata Item
//    public JsonNode getServerMetadataItem(String token, String serverId, String key) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/metadata/" + key;
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // Update Server Metadata Item
//    public JsonNode updateServerMetadataItem(String token, String serverId, String key) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/metadata/" + key;
//        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // List Flavors
//    public JsonNode listFlavors(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/flavors";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    // List Flavors without query parameters
//    public JsonNode listFlavors(String token) {
//        return listFlavors(token, Collections.emptyMap());
//    }
//
//    // Get Flavor Details
//    public JsonNode getFlavorDetails(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/flavors/detail";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    // Get Flavor Details without query parameters
//    public JsonNode getFlavorDetails(String token) {
//        return getFlavorDetails(token, Collections.emptyMap());
//    }
//
//    // List Key Pairs
//    public JsonNode listKeyPairs(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/os-keypairs";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    // List Key Pairs without query parameters
//    public JsonNode listKeyPairs(String token) {
//        return listKeyPairs(token, Collections.emptyMap());
//    }
//
//    // Get Key Pair Details
//    public JsonNode getKeyPairDetails(String token, String keyPairName) {
//        String uri = "/compute/v2.1/os-keypairs/" + keyPairName;
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // Delete Key Pair
//    public JsonNode deleteKeyPair(String token, String keyPairName) {
//        String uri = "/compute/v2.1/os-keypairs/" + keyPairName;
//        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token));
//    }
//
//    // List Volume Attachments
//    public JsonNode listVolumeAttachments(String token, String serverId) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/os-volume_attachments";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // Get Volume Attachment Details
//    public JsonNode getVolumeAttachmentDetails(String token, String serverId, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/os-volume_attachments/";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    public JsonNode getVolumeAttachmentDetails(String token, String serverId) {
//        return getVolumeAttachmentDetails(token, serverId, Collections.emptyMap());
//    }
//
//    // Create Server
//    public JsonNode createServer(String token, CreateServerRequest request) {
//        String uri = "/compute/v2.1/servers";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Evacuate Server
//    public JsonNode evacuateServer(String token, String serverId, EvacuateServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Force Delete Server
//    public JsonNode forceDeleteServer(String token, String serverId, ForceDeleteServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Restore Server
//    public JsonNode restoreServer(String token, String serverId, RestoreServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Rescue Server
//    public JsonNode rescueServer(String token, String serverId, RescueServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Unrescue Server
//    public JsonNode unrescueServer(String token, String serverId, UnrescueServerRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Inject Network Info
//    public JsonNode injectNetworkInfo(String token, String serverId, InjectNetworkInfoRequest request) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Create Key Pair
//    public JsonNode createKeyPair(String token, CreateKeyPairRequest request) {
//        String uri = "/compute/v2.1/os-keypairs";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//
//    // List Flavor Details
//    public JsonNode listFlavorsDetail(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/flavors/detail";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    public JsonNode listFlavorsDetail(String token) {
//        return listFlavorsDetail(token, Collections.emptyMap());
//    }
//
//    // Get Server Diagnostics
//    public JsonNode getServerDiagnostics(String token, String serverId) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/diagnostics";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // Get Server Instance Actions
//    public JsonNode getServerInstanceActions(String token, String serverId, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/os-instance-actions";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    public JsonNode getServerInstanceActions(String token, String serverId) {
//        return getServerInstanceActions(token, serverId, Collections.emptyMap());
//    }
//
//    // Get Server Instance Action Details
//    public JsonNode getServerInstanceActionDetails(String token, String serverId, String requestId) {
//        String uri = "/compute/v2.1/servers/" + serverId + "/os-instance-actions/" + requestId;
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // Get Server Limits
//    public JsonNode getServerLimits(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/limits";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    public JsonNode getServerLimits(String token) {
//        return getServerLimits(token, Collections.emptyMap());
//    }
//
//    // Get Server Usage
//    public JsonNode getServerUsage(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/os-simple-tenant-usage/";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    public JsonNode getServerUsage(String token) {
//        return getServerUsage(token, Collections.emptyMap());
//    }
//
//    // Get Server Usage with Details
//    public JsonNode getServerUsageDetail(String token, String tenantId, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/os-simple-tenant-usage/" + tenantId;
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    public JsonNode getServerUsageDetail(String token, String tenantId) {
//        return getServerUsageDetail(token, tenantId, Collections.emptyMap());
//    }
//
//    // List Availability Zones
//    public JsonNode listAvailabilityZones(String token) {
//        String uri = "/compute/v2.1/os-availability-zone";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // Get Availability Zone Details
//    public JsonNode getAvailabilityZoneDetails(String token) {
//        String uri = "/compute/v2.1/os-availability-zone/detail";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // List Hypervisors
//    public JsonNode listHypervisors(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/os-hypervisors";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    public JsonNode listHypervisors(String token) {
//        return listHypervisors(token, Collections.emptyMap());
//    }
//
//    // List Hypervisors with Details
//    public JsonNode listHypervisorsDetail(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/os-hypervisors/detail";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    public JsonNode listHypervisorsDetail(String token) {
//        return listHypervisorsDetail(token, Collections.emptyMap());
//    }
//
//    // Get Hypervisor Details
//    public JsonNode getHypervisorDetails(String token, String hypervisorId) {
//        String uri = "/compute/v2.1/os-hypervisors/" + hypervisorId;
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // List Server Groups
//    public JsonNode listServerGroups(String token, Map<String, String> queryParams) {
//        String uri = "/compute/v2.1/os-server-groups";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams);
//    }
//
//    public JsonNode listServerGroups(String token) {
//        return listServerGroups(token, Collections.emptyMap());
//    }
//
//    // Create Server Group
//    public JsonNode createServerGroup(String token, CreateServerGroupRequest request) {
//        String uri = "/compute/v2.1/os-server-groups";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Get Server Group Details
//    public JsonNode getServerGroupDetails(String token, String groupId) {
//        String uri = "/compute/v2.1/os-server-groups/" + groupId;
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // Delete Server Group
//    public JsonNode deleteServerGroup(String token, String groupId) {
//        String uri = "/compute/v2.1/os-server-groups/" + groupId;
//        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token));
//    }
//
//    // List Aggregate Hosts
//    public JsonNode listAggregateHosts(String token) {
//        String uri = "/compute/v2.1/os-aggregates";
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // Get Aggregate Details
//    public JsonNode getAggregateDetails(String token, String aggregateId) {
//        String uri = "/compute/v2.1/os-aggregates/" + aggregateId;
//        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap());
//    }
//
//    // Create Aggregate
//    public JsonNode createAggregate(String token, CreateAggregateRequest request) {
//        String uri = "/compute/v2.1/os-aggregates";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Update Aggregate
//    public JsonNode updateAggregate(String token, String aggregateId, Object request) {
//        String uri = "/compute/v2.1/os-aggregates/" + aggregateId;
//        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Delete Aggregate
//    public JsonNode deleteAggregate(String token, String aggregateId) {
//        String uri = "/compute/v2.1/os-aggregates/" + aggregateId;
//        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token));
//    }
//
//    // Add Host to Aggregate
//    public JsonNode addHostToAggregate(String token, String aggregateId, AddHostToAggregateRequest request) {
//        String uri = "/compute/v2.1/os-aggregates/" + aggregateId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Remove Host from Aggregate
//    public JsonNode removeHostFromAggregate(String token, String aggregateId, RemoveHostFromAggregate request) {
//        String uri = "/compute/v2.1/os-aggregates/" + aggregateId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }
//
//    // Set Aggregate Metadata
//    public JsonNode setAggregateMetadata(String token, String aggregateId, SetAggregateMetadata request) {
//        String uri = "/compute/v2.1/os-aggregates/" + aggregateId + "/action";
//        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request);
//    }

}
