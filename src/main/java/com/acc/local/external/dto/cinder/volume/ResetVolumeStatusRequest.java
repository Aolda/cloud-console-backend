package com.acc.local.external.dto.cinder.volume;

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
public class ResetVolumeStatusRequest {

    @JsonProperty("os-reset_status")
    private ResetStatus resetStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ResetStatus {
        @JsonProperty("status")
        private String status;

        @JsonProperty("attach_status")
        private String attachStatus;

        @JsonProperty("migration_status")
        private String migrationStatus;
    }
}
