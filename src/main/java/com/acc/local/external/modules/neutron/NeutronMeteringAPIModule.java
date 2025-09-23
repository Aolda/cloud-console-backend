package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.metering.CreateMeteringLabelRequest;
import com.acc.local.external.dto.neutron.metering.CreateMeteringLabelRuleRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronMeteringAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listMeteringLabels(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/metering/metering-labels";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createMeteringLabel(String token, CreateMeteringLabelRequest request) {
        String uri = "/v2.0/metering/metering-labels";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showMeteringLabel(String token, String meteringLabelId) {
        String uri = "/v2.0/metering/metering-labels/" + meteringLabelId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> deleteMeteringLabel(String token, String meteringLabelId) {
        String uri = "/v2.0/metering/metering-labels/" + meteringLabelId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> listMeteringLabelRules(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/metering/metering-label-rules";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createMeteringLabelRule(String token, CreateMeteringLabelRuleRequest request) {
        String uri = "/v2.0/metering/metering-label-rules";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showMeteringLabelRule(String token, String meteringLabelRuleId) {
        String uri = "/v2.0/metering/metering-label-rules/" + meteringLabelRuleId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> deleteMeteringLabelRule(String token, String meteringLabelRuleId) {
        String uri = "/v2.0/metering/metering-label-rules/" + meteringLabelRuleId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}