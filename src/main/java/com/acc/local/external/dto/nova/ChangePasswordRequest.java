package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {
    private ChangePassword changePassword;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChangePassword {
        private String adminPass;
    }
} 