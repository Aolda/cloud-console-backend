package com.acc.local.external.dto.cinder.backup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportBackupRequest {

    @JsonProperty("backup-record")
    private BackupRecord backupRecord;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BackupRecord {
        @JsonProperty("backup_service")
        private String backupService;
        @JsonProperty("backup_url")
        private String backupUrl;
    }
}
