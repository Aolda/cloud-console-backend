package com.acc.local.external.modules.nova;

import com.acc.local.external.dto.nova.flavor.FlavorAccessRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class NovaFlavorAccessModule extends NovaAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    // Tenant Access 추가 (Private Flavor용)
    public ResponseEntity<JsonNode> addTenantAccess(String token, String flavorId, FlavorAccessRequest request) {
        String uri = "/v2.1/flavors/" + flavorId + "/action";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }
}
