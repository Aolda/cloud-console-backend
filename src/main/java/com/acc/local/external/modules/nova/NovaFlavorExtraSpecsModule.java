package com.acc.local.external.modules.nova;

import com.acc.local.external.dto.nova.flavor.FlavorExtraSpecsRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NovaFlavorExtraSpecsModule extends NovaAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    // Extra Specs 생성
    public ResponseEntity<JsonNode> createExtraSpecs(String token, String flavorId, FlavorExtraSpecsRequest request) {
        String uri = "/v2.1/flavors/" + flavorId + "/os-extra_specs";
        return openstackAPICallModule.callPostAPI(uri, createHeaders(token), request, port);
    }

    private Map<String, String> createHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", token);
        headers.put("X-OpenStack-Nova-API-Version", "2.79");
        return headers;
    }
}
