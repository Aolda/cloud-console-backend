package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoveSecurityGroupRequest {
    private RemoveSecurityGroup removeSecurityGroup;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RemoveSecurityGroup {
        private String name;
    }
} 