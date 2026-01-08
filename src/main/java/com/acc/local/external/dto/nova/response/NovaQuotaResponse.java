package com.acc.local.external.dto.nova.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovaQuotaResponse {

    @JsonProperty("quota_set")
    private QuotaSet quotaSet;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotaSet {
        private Integer cores;
        private Integer ram;
        private Integer instances;

        @JsonProperty("key_pairs")
        private Integer keyPairs;

        @JsonProperty("server_groups")
        private Integer serverGroups;

        @JsonProperty("server_group_members")
        private Integer serverGroupMembers;

        @JsonProperty("metadata_items")
        private Integer metadataItems;

        @JsonProperty("injected_files")
        private Integer injectedFiles;

        @JsonProperty("injected_file_content_bytes")
        private Integer injectedFileContentBytes;

        @JsonProperty("injected_file_path_bytes")
        private Integer injectedFilePathBytes;
    }
}