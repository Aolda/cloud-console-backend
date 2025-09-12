package com.acc.local.external.dto.storage.volume;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachVolumeRequest {

    @JsonProperty("os-attach")
    private OsAttach osAttach;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsAttach {
        @JsonProperty("instance_uuid")
        private String instanceUuid;

        @JsonProperty("mountpoint")
        private String mountpoint;

        @JsonProperty("host_name")
        private String hostName;
    }
}
