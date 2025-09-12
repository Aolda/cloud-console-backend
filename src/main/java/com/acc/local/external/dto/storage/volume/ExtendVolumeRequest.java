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
public class ExtendVolumeRequest {
    @JsonProperty("os-extend")
    private OsExtend osExtend;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OsExtend {
        @JsonProperty("new_size")
        private Integer newSize;
    }
}
