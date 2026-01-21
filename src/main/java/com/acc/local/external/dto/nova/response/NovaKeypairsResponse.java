package com.acc.local.external.dto.nova.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class NovaKeypairsResponse {
    private List<KeypairWrapper> keypairs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KeypairWrapper {
        private Keypair keypair;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Keypair {
        private String name;
        @JsonProperty("public_key")
        private String publicKey;
        private String fingerprint;
        // Nova returns private_key only on create when Nova generates the keypair.
        // Absent for imported keys or for list/get operations.
        @JsonProperty("private_key")
        private String privateKey;
    }
}
