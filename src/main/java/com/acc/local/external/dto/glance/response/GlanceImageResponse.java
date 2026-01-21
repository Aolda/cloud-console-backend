package com.acc.local.external.dto.glance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlanceImageResponse {
    private String id;
    private String name;
    private String status;
    private String visibility;
    private Long size;

    @JsonProperty("virtual_size")
    private Long virtualSize;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("checksum")
    private String checksum;

    @JsonProperty("container_format")
    private String containerFormat;

    @JsonProperty("disk_format")
    private String diskFormat;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("min_ram")
    private Integer minRam;

    @JsonProperty("min_disk")
    private Integer minDisk;

    @JsonProperty("protected")
    private Boolean protectedImage;

    @JsonProperty("os_hidden")
    private Boolean osHidden;

    @JsonProperty("os_distro")
    private String osDistro;

    @JsonProperty("os_version")
    private String osVersion;

    @JsonProperty("os_admin_user")
    private String osAdminUser;

    @JsonProperty("usage_type")
    private String usageType;

    @JsonProperty("hw_qemu_guest_agent")
    private String hwQemuGuestAgent;

    @JsonProperty("os_hash_algo")
    private String osHashAlgo;

    @JsonProperty("os_hash_value")
    private String osHashValue;

    private List<String> tags;

    @JsonProperty("self")
    private String self;

    @JsonProperty("file")
    private String file;

    @JsonProperty("schema")
    private String schema;

    @JsonProperty("stores")
    private String stores;
}