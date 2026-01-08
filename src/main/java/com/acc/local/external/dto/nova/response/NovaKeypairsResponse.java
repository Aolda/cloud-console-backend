package com.acc.local.external.dto.nova.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NovaKeypairsResponse {

    private List<KeypairWrapper> keypairs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeypairWrapper {
        private Keypair keypair;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keypair {
        private String name;
        private String fingerprint;

        @JsonProperty("public_key")
        private String publicKey;

        @JsonProperty("private_key")
        private String privateKey;

        @JsonProperty("user_id")
        private String userId;

        private String type;
    }
}