package com.acc.local.external.modules.nova;

import com.acc.local.external.dto.nova.keypair.CreateKeyPairRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NovaKeypairAPIModule extends NovaAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    // Create Key Pair
    public ResponseEntity<JsonNode> createKeyPair(String token, CreateKeyPairRequest request) {
        String uri = "/v2.1/os-keypairs";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    // List Key Pairs
    public ResponseEntity<JsonNode> listKeyPairs(String token, Map<String, String> queryParams) {
        String uri = "/v2.1/os-keypairs";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    // List Key Pairs without query parameters
    public ResponseEntity<JsonNode> listKeyPairs(String token) {
        return listKeyPairs(token, Collections.emptyMap());
    }

    // Get Key Pair Details
    public ResponseEntity<JsonNode> getKeyPairDetails(String token, String keyPairName) {
        String uri = "/v2.1/os-keypairs/" + keyPairName;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    // Delete Key Pair
    public ResponseEntity<JsonNode> deleteKeyPair(String token, String keyPairName) {
        String uri = "/v2.1/os-keypairs/" + keyPairName;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}
