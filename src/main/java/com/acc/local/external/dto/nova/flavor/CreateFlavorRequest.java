package com.acc.local.external.dto.nova.flavor;

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
public class CreateFlavorRequest {
    private Flavor flavor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Flavor {
        private String name;
        private Integer ram;
        private Integer vcpus;
        private Integer disk;

        // ID를 null로 보내면 OpenStack이 UUID 자동 생성
        private String id;
        @JsonProperty("os-flavor-access:is_public")
        private Boolean isPublic;
        private String description;
    }
}
