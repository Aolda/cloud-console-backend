package com.acc.local.external.dto.storage.snapshot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSnapshotMetadataKeyRequest {

    @JsonProperty("meta")
    private Map<String, String> meta;
}
