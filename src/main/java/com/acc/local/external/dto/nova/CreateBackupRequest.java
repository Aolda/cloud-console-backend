package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBackupRequest {
    private CreateBackup createBackup;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateBackup {
        private String name;
        private String backup_type;
        private int rotation;
    }
} 