package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.addressscopes.CreateAddressScopeRequest;
import com.acc.local.external.dto.neutron.addressscopes.UpdateAddressScopeRequest;
import com.acc.local.external.dto.neutron.logging.CreateLogRequest;
import com.acc.local.external.dto.neutron.logging.UpdateLogRequest;
import com.acc.local.external.dto.neutron.segments.CreateSegmentRequest;
import com.acc.local.external.dto.neutron.segments.UpdateSegmentRequest;
import com.acc.local.external.dto.neutron.sfc.CreateFlowClassifierRequest;
import com.acc.local.external.dto.neutron.sfc.CreateServiceGraphRequest;
import com.acc.local.external.dto.neutron.sfc.UpdateFlowClassifierRequest;
import com.acc.local.external.dto.neutron.sfc.UpdateServiceGraphRequest;
import com.acc.local.external.dto.neutron.tags.CreateTagsRequest;
import com.acc.local.external.dto.neutron.tags.ReplaceTagsRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronEtcAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    // Segments
    public ResponseEntity<JsonNode> listSegments(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/segments";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createSegment(String token, CreateSegmentRequest request) {
        String uri = "/v2.0/segments";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showSegment(String token, String segmentId) {
        String uri = "/v2.0/segments/" + segmentId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateSegment(String token, String segmentId, UpdateSegmentRequest request) {
        String uri = "/v2.0/segments/" + segmentId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteSegment(String token, String segmentId) {
        String uri = "/v2.0/segments/" + segmentId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    // Trunks
    public ResponseEntity<JsonNode> showTrunk(String token, String trunkId) {
        String uri = "/v2.0/trunks/" + trunkId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    // Address Scopes
    public ResponseEntity<JsonNode> listAddressScopes(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/address-scopes";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createAddressScope(String token, CreateAddressScopeRequest request) {
        String uri = "/v2.0/address-scopes";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showAddressScope(String token, String addressScopeId) {
        String uri = "/v2.0/address-scopes/" + addressScopeId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateAddressScope(String token, String addressScopeId, UpdateAddressScopeRequest request) {
        String uri = "/v2.0/address-scopes/" + addressScopeId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteAddressScope(String token, String addressScopeId) {
        String uri = "/v2.0/address-scopes/" + addressScopeId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    // Neutron IP Availability
    public ResponseEntity<JsonNode> showNeutronIpAvailability(String token, String networkId) {
        String uri = "/v2.0/network-ip-availabilities/" + networkId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> listNeutronIpAvailabilities(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/network-ip-availabilities";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    // Service Providers
    public ResponseEntity<JsonNode> listServiceProviders(String token) {
        String uri = "/v2.0/service-providers";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    // Tags
    public ResponseEntity<JsonNode> createTags(String token, String resourceType, String resourceId, CreateTagsRequest request) {
        String uri = "/v2.0/" + resourceType + "/" + resourceId + "/tags";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> replaceAllTags(String token, String resourceType, String resourceId, ReplaceTagsRequest request) {
        String uri = "/v2.0/" + resourceType + "/" + resourceId + "/tags";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> removeAllTags(String token, String resourceType, String resourceId) {
        String uri = "/v2.0/" + resourceType + "/" + resourceId + "/tags";
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> checkTagExists(String token, String resourceType, String resourceId, String tag) {
        String uri = "/v2.0/" + resourceType + "/" + resourceId + "/tags/" + tag;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> addTag(String token, String resourceType, String resourceId, String tag) {
        String uri = "/v2.0/" + resourceType + "/" + resourceId + "/tags/" + tag;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), null, port);
    }

    public ResponseEntity<JsonNode> listTags(String token, String resourceType, String resourceId) {
        String uri = "/v2.0/" + resourceType + "/" + resourceId + "/tags";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> removeTag(String token, String resourceType, String resourceId, String tag) {
        String uri = "/v2.0/" + resourceType + "/" + resourceId + "/tags/" + tag;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    // Logging
    public ResponseEntity<JsonNode> listLogs(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/log/logs";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createLog(String token, CreateLogRequest request) {
        String uri = "/v2.0/log/logs";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showLog(String token, String logId) {
        String uri = "/v2.0/log/logs/" + logId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateLog(String token, String logId, UpdateLogRequest request) {
        String uri = "/v2.0/log/logs/" + logId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteLog(String token, String logId) {
        String uri = "/v2.0/log/logs/" + logId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> listLoggableResources(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/log/loggable-resources";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    // SFC
    public ResponseEntity<JsonNode> listFlowClassifiers(String token, Map<String, String> queryParams) {
        String uri = "/v1.0/sfc/flow_classifiers";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createFlowClassifier(String token, CreateFlowClassifierRequest request) {
        String uri = "/v1.0/sfc/flow_classifiers";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showFlowClassifier(String token, String flowClassifierId) {
        String uri = "/v1.0/sfc/flow_classifiers/" + flowClassifierId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateFlowClassifier(String token, String flowClassifierId, UpdateFlowClassifierRequest request) {
        String uri = "/v1.0/sfc/flow_classifiers/" + flowClassifierId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteFlowClassifier(String token, String flowClassifierId) {
        String uri = "/v1.0/sfc/flow-classifiers/" + flowClassifierId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> listServiceGraphs(String token, Map<String, String> queryParams) {
        String uri = "/v1.0/sfc/service_graphs";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createServiceGraph(String token, CreateServiceGraphRequest request) {
        String uri = "/v1.0/sfc/service_graphs";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showServiceGraph(String token, String serviceGraphId) {
        String uri = "/v1.0/sfc/service_graphs/" + serviceGraphId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateServiceGraph(String token, String serviceGraphId, UpdateServiceGraphRequest request) {
        String uri = "/v1.0/sfc/service_graphs/" + serviceGraphId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteServiceGraph(String token, String serviceGraphId) {
        String uri = "/v1.0/sfc/service_graphs/" + serviceGraphId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }
}