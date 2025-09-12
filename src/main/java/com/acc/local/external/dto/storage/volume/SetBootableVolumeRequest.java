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
public class SetBootableVolumeRequest {

    @JsonProperty("os-set_bootable")
    private OsSetBootable osSetBootable;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsSetBootable {
        @JsonProperty("bootable")
        private Boolean bootable;
    }
}
