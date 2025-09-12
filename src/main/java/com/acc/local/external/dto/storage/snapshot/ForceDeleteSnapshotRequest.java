package com.acc.local.external.dto.storage.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForceDeleteSnapshotRequest {

    @JsonProperty("os-force_delete")
    private Object osForceDelete;
}
