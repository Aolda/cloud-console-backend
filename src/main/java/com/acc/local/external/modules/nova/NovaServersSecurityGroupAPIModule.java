package com.acc.local.external.modules.nova;

import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class NovaServersSecurityGroupAPIModule extends NovaAPIUtil
{

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listSecurityGroups(String token, String serverId) {
        String uri = "/v2.1/servers/" + serverId + "/os-security-groups";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }
}
