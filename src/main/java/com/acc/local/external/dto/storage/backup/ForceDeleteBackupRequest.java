package com.acc.local.external.dto.storage.backup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForceDeleteBackupRequest {

    @JsonProperty("os-force_delete")
    private Object osForceDelete;
}
