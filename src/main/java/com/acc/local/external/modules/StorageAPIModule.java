package com.acc.local.external.modules;

import com.acc.local.external.dto.storage.attachment.CompleteAttachmentRequest;
import com.acc.local.external.dto.storage.attachment.CreateAttachmentRequest;
import com.acc.local.external.dto.storage.attachment.UpdateAttachmentRequest;
import com.acc.local.external.dto.storage.backup.*;
import com.acc.local.external.dto.storage.defaultTypes.UpdateVolumeTypeRequest;
import com.acc.local.external.dto.storage.quota.UpdateQuotaClassRequest;
import com.acc.local.external.dto.storage.quota.UpdateQuotaRequest;
import com.acc.local.external.dto.storage.service.ServiceRequest;
import com.acc.local.external.dto.storage.snapshot.*;
import com.acc.local.external.dto.storage.volume.*;
import com.acc.local.external.dto.storage.worker.CleanupWorkerRequest;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StorageAPIModule {

    private final OpenstackAPICallModule openstackAPICall;

    private Map<String, String> headers(String token) {
        return Map.of("X-Auth-Token", token);
    }


    private Map<String, String> q(Map<String, String> query) {
        return (query == null) ? Collections.emptyMap() : query;
    }

    // ============ Default Types ============
    public JsonNode getDefaultType(String token, String projectId) {
        String uri = "/v3/default-types/" + projectId;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    public JsonNode updateVolumeType(String token, String projectId,UpdateVolumeTypeRequest request) {
        String uri = "/v3/default-types/" + projectId;
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode listDefaultTypes(String token, Map<String, String> query) {
        String uri = "/v3/default-types/";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode deleteDefaultType(String token, String projectId) {
        String uri = "/v3/default-types/" + projectId;
        return openstackAPICall.callDeleteAPI(uri, headers(token));
    }

    // ============ Volumes ============
    public JsonNode listVolumesDetail(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/volumes/detail";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode createVolume(String token, String projectId, CreateVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode listVolumes(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/volumes";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode getVolume(String token, String projectId, String volumeId) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    public JsonNode updateVolume(String token, String projectId, String volumeId, com.acc.local.external.dto.storage.volume.UpdateVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId;
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode deleteVolume(String token, String projectId, String volumeId) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId;
        return openstackAPICall.callDeleteAPI(uri, headers(token));
    }

    public JsonNode createVolumeMetadata(String token, String projectId, String volumeId, VolumeMetadataRequest request){
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata";
        return openstackAPICall.callPostAPI(uri,headers(token), request);
    }

    public JsonNode getVolumeMetadata(String token, String projectId, String volumeId) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata";
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }
    public JsonNode updateVolumeMetadata(String token, String projectId, String volumeId, VolumeMetadataRequest request){
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata";
        return openstackAPICall.callPutAPI(uri,headers(token), request);
    }


    public JsonNode getVolumeMetadataKey(String token, String projectId, String volumeId, String key) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata/" + key;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    public JsonNode deleteVolumeMetadataKey(String token, String projectId, String volumeId, String key) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata/" + key;
        return openstackAPICall.callDeleteAPI(uri, headers(token));
    }

    public JsonNode updateVolumeMetadataKey(String token, String projectId, String volumeId, String key, UpdateVolumeMetadataKeyRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/metadata/" + key;
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    /** GET /v3/{project_id}/volumes/summary */
    public JsonNode getVolumesSummary(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/volumes/summary";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    // ============ Actions ============
    public JsonNode extendVolume(String token, String projectId, String volumeId, ExtendVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode completeExtendVolume(String token, String projectId, String volumeId, ExtendVolumeCompletionRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode resetVolumeStatus(String token, String projectId, String volumeId, ResetVolumeStatusRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode revertVolumeToSnapshot(String token, String projectId, String volumeId, RevertVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode setImageMetadata(String token, String projectId, String volumeId, SetImageMetadataRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode unsetImageMetadata(String token, String projectId, String volumeId, UnsetImageMetadataRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode showImageMetadata(String token, String projectId, String volumeId, ShowImageMetadataRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }
    public JsonNode attachVolume(String token, String projectId, String volumeId, AttachVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode detachVolume(String token, String projectId, String volumeId, DetachVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode unmanageVolume(String token, String projectId, String volumeId, UnmanageVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode forceDetachVolume(String token, String projectId, String volumeId, ForceDetachVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode retypeVolume(String token, String projectId, String volumeId, RetypeVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode migrateVolume(String token, String projectId, String volumeId, MigrateVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode completeMigrateVolume(String token, String projectId, String volumeId, CompleteMigrateVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode forceDeleteVolume(String token, String projectId, String volumeId, ForceDeleteVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode setBootableVolume(String token, String projectId, String volumeId, SetBootableVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode uploadVolumeToImage(String token, String projectId, String volumeId, UploadVolumeToImageRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode reserveVolume(String token, String projectId, String volumeId, ReserveVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode unreserveVolume(String token, String projectId, String volumeId, UnreserveVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode beginDetachingVolume(String token, String projectId, String volumeId, BeginDetachingVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode rollDetachingVolume(String token, String projectId, String volumeId, RollDetachingVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode terminateVolumeConnection(String token, String projectId, String volumeId, TerminateConnectionRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode initializeVolumeConnection(String token, String projectId, String volumeId, InitializeConnectionRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode updateVolumeReadonlyFlag(String token, String projectId, String volumeId, UpdateReadonlyFlagRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode reimageVolume(String token, String projectId, String volumeId, ReimageVolumeRequest request) {
        String uri = "/v3/" + projectId + "/volumes/" + volumeId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    // ============ Snapshots ============
    public JsonNode listSnapshotsDetail(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/snapshots/detail";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode createSnapshot(String token, String projectId, CreateSnapshotRequest request) {
        String uri = "/v3/" + projectId + "/snapshots";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode listSnapshots(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/snapshots";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode getSnapshotMetadata(String token, String projectId, String snapshotId) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata";
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }
    public JsonNode createSnapshotMetadata(String token, String projectId, String snapshotId, CreateSnapshotMetadataRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode getSnapshot(String token, String projectId, String snapshotId) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }
    public JsonNode updateSnapshotMetadata(String token, String projectId, String snapshotId, UpdateSnapshotMetadataRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata";
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }
    public JsonNode updateSnapshot(String token, String projectId, String snapshotId, UpdateSnapshotRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId;
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode getSnapshotMetadataKey(String token, String projectId, String snapshotId, String key) {
        String uri = "/v3/" + projectId + "/snapshot/" + snapshotId + "/metadata/" + key;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }
    public JsonNode updateSnapshotMetadataKey(String token, String projectId, String snapshotId, String key, UpdateSnapshotMetadataKeyRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata/" + key;
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }
    public JsonNode deleteSnapshot(String token, String projectId, String snapshotId) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId;
        return openstackAPICall.callDeleteAPI(uri, headers(token));
    }

    public JsonNode deleteSnapshotMetadataKey(String token, String projectId, String snapshotId, String key) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/metadata/" + key;
        return openstackAPICall.callDeleteAPI(uri, headers(token));
    }

    public JsonNode resetSnapshotStatus(String token, String projectId, String snapshotId, ResetSnapshotStatusRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode updateSnapshotStatus(String token, String projectId, String snapshotId, UpdateSnapshotStatusRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode forceDeleteSnapshot(String token, String projectId, String snapshotId, ForceDeleteSnapshotRequest request) {
        String uri = "/v3/" + projectId + "/snapshots/" + snapshotId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }


    // ============ Attachments ============
    public JsonNode getAttachment(String token, String projectId, String attachmentId) {
        String uri = "/v3/" + projectId + "/attachments/" + attachmentId;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }
    public JsonNode createAttachment(String token, String projectId, CreateAttachmentRequest request) {
        String uri = "/v3/" + projectId + "/attachments";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode listAttachmentsDetail(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/attachments/detail";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode listAttachments(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/attachments";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode deleteAttachment(String token, String projectId, String attachmentId) {
        String uri = "/v3/" + projectId + "/attachments/" + attachmentId;
        return openstackAPICall.callDeleteAPI(uri, headers(token));
    }

    public JsonNode updateAttachment(String token, String projectId, String attachmentId, UpdateAttachmentRequest request) {
        String uri = "/v3/" + projectId + "/attachments/" + attachmentId;
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode completeAttachment(String token, String projectId, String attachmentId, CompleteAttachmentRequest request) {
        String uri = "/v3/" + projectId + "/attachments/" + attachmentId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    // ============ Backups ============
    public JsonNode listBackupsDetail(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/backups/detail";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }
    public JsonNode restoreBackup(String token, String projectId, String backupId, RestoreBackupRequest request) {
        String uri = "/v3/" + projectId + "/backups/" + backupId + "/restore";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }
    public JsonNode createBackup(String token, String projectId, CreateBackupRequest request) {
        String uri = "/v3/" + projectId + "/backups";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode getBackup(String token, String projectId, String backupId) {
        String uri = "/v3/" + projectId + "/backups/" + backupId;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    public JsonNode updateBackup(String token, String projectId, String backupId, UpdateBackupRequest request) {
        String uri = "/v3/" + projectId + "/backups/" + backupId;
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode importBackup(String token, String projectId, ImportBackupRequest request) {
        String uri = "/v3/" + projectId + "/backups/import_record";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode listBackups(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/backups";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode exportBackupRecord(String token, String projectId, String backupId) {
        String uri = "/v3/" + projectId + "/backups/" + backupId + "/export_record";
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    public JsonNode deleteBackup(String token, String projectId, String backupId) {
        String uri = "/v3/" + projectId + "/backups/" + backupId;
        return openstackAPICall.callDeleteAPI(uri, headers(token));
    }
    public JsonNode forceDeleteBackup(String token, String projectId, String backupId, ForceDeleteBackupRequest request) {
        String uri = "/v3/" + projectId + "/backups/" + backupId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    public JsonNode resetBackupStatus(String token, String projectId, String backupId, ResetBackupStatusRequest request) {
        String uri = "/v3/" + projectId + "/backups/" + backupId + "/action";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

    // ============ Capabilities / Services ============
    public JsonNode getBackendCapabilities(String token, String projectId, String hostname) {
        String uri = "/v3/" + projectId + "/capabilities/" + hostname;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    public JsonNode listCinderServices(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/os-services";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode disableService(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/disable";
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }
    public JsonNode disableServiceLogReason(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/disable-log-reason";
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }
    public JsonNode enableService(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/enable";
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode getServiceLog(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/get-log";
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode setServiceLog(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/set-log";
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode freezeHost(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/freeze";
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode thawHost(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/thaw";
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode failoverHost(String token, String projectId, ServiceRequest request) {
        String uri = "/v3/" + projectId + "/os-services/failover_host";
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }



    // ============ Hosts (admin) ============
    public JsonNode listHosts(String token, String adminProjectId, Map<String, String> query) {
        String uri = "/v3/" + adminProjectId + "/os-hosts";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode getHost(String token, String adminProjectId, String hostName) {
        String uri = "/v3/" + adminProjectId + "/os-hosts/" + hostName;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    // ============ Limits ============
    public JsonNode getProjectLimits(String token, String projectId) {
        String uri = "/v3/" + projectId + "/limits";
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    // ============ Messages ============
    public JsonNode getMessage(String token, String projectId, String messageId) {
        String uri = "/v3/" + projectId + "/messages/" + messageId;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    public JsonNode listMessages(String token, String projectId, Map<String, String> query) {
        String uri = "/v3/" + projectId + "/messages";
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }

    public JsonNode deleteMessage(String token, String projectId, String messageId) {
        String uri = "/v3/" + projectId + "/messages/" + messageId;
        return openstackAPICall.callDeleteAPI(uri, headers(token));
    }


    // ============ Resource Filters ============
    public JsonNode listResourceFilters(String token, String projectId) {
        String uri = "/v3/" + projectId + "/resource_filters";
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    // ============ Quotas (admin) ============
    public JsonNode getQuotaClassSet(String token, String adminProjectId, String quotaClassName) {
        String uri = "/v3/" + adminProjectId + "/os-quota-class-sets/" + quotaClassName;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    public JsonNode getQuotaSet(String token, String adminProjectId, String projectId) {
        String uri = "/v3/" + adminProjectId + "/os-quota-sets/" + projectId;
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    public JsonNode getQuotaUsage(String token, String adminProjectId, String projectId, boolean usage) {
        String uri = "/v3/" + adminProjectId + "/os-quota-sets/" + projectId;
        Map<String, String> query = new HashMap<>();
        if (usage) query.put("usage", "True");
        return openstackAPICall.callGetAPI(uri, headers(token), q(query));
    }
    public JsonNode updateProjectQuota(String token, String adminProjectId, String projectId, UpdateQuotaRequest request) {
        String uri = "/v3/" + adminProjectId + "/os-quota-sets/" + projectId;
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }

    public JsonNode getQuotaDefaults(String token, String adminProjectId, String projectId) {
        String uri = "/v3/" + adminProjectId + "/os-quota-sets/" + projectId + "/defaults";
        return openstackAPICall.callGetAPI(uri, headers(token), Collections.emptyMap());
    }

    public JsonNode updateQuotaClass(String token, String adminProjectId, String quotaClassName, UpdateQuotaClassRequest request) {
        String uri = "/v3/" + adminProjectId + "/os-quota-class-sets/" + quotaClassName;
        return openstackAPICall.callPutAPI(uri, headers(token), request);
    }


    public JsonNode deleteQuotaSet(String token, String adminProjectId, String projectId) {
        String uri = "/v3/" + adminProjectId + "/os-quota-sets/" + projectId;
        return openstackAPICall.callDeleteAPI(uri, headers(token));
    }

    public JsonNode cleanupWorker(String token, String projectId, CleanupWorkerRequest request) {
        String uri = "/v3/" + projectId + "/workers/cleanup";
        return openstackAPICall.callPostAPI(uri, headers(token), request);
    }

}
