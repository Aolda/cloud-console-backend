package com.acc.local.external.dto.nova.serverAction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RebuildServerRequest {
    private Rebuild rebuild;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Rebuild {
        private String imageRef;
        private String name;
        private String adminPass;
        private Boolean preserveEphemeral;
        private String accessIPv4;
        private String accessIPv6;
        private String userData;
        private String keyName;
        private List<String> trusted_image_certificates;
        // 추가 필드가 필요하면 여기에 추가
    }
}
