package com.acc.local.external.dto.cinder.snapshot;

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

public class ResetSnapshotStatusRequest {

    @JsonProperty("os-reset_status")
    private OsResetStatus osResetStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

    public static class OsResetStatus {
        private String status;
    }
}
