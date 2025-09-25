package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateServerGroupRequest {
    private ServerGroup server_group;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServerGroup {
        private String name;
        private List<String> policy;
        private Rule rules;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Rule {
        private Integer max_server_per_host;
        private String anti_affinity; // 변수명 변환 필요
    }
}
