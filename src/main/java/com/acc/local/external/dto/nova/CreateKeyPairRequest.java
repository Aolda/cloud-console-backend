package com.acc.local.external.dto.nova;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateKeyPairRequest {
    private KeyPair keypair;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KeyPair {
        private String name;
        private String publicKey;
        private String type;
        private String userId;
    }
} 