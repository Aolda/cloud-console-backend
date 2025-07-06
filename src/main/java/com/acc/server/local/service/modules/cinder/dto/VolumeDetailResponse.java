package com.acc.server.local.service.modules.cinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class VolumeDetailResponse {
    private String id;
    private String status;
    private int size;

    @JsonProperty("availability_zone")
    private String availabilityZone;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    private String name;
    private String description;

    @JsonProperty("volume_type")
    private String volumeType;

    @JsonProperty("snapshot_id")
    private String snapshotId;

    @JsonProperty("source_volid")
    private String sourceVolid;

    private Map<String, Object> metadata;
    private List<VolumeResponseLinkDto> links;

    @JsonProperty("user_id")
    private String userId;

    private String bootable;
    private boolean encrypted;

    @JsonProperty("replication_status")
    private String replicationStatus;

    @JsonProperty("consistencygroup_id")
    private String consistencyGroupId;

    private boolean multiattach;
    private List<Object> attachments;

    @JsonProperty("migration_status")
    private String migrationStatus;

    @JsonProperty("os-vol-host-attr:host")
    private String host;

    @JsonProperty("os-vol-tenant-attr:tenant_id")
    private String tenantId;

    @JsonProperty("os-vol-mig-status-attr:migstat")
    private String migstat;

    @JsonProperty("os-vol-mig-status-attr:name_id")
    private String nameId;
}
