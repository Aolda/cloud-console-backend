package com.acc.local.external.dto.nova.adminServerAction;

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
public class LiveMigrateServerRequest {

    @JsonProperty("os-migrateLive")
    private LiveMigrate live_migrate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LiveMigrate {
        private String host;
        private boolean block_migration;
        private boolean disk_over_commit;
        private boolean force;
    }
}
