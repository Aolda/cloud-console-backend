package com.acc.local.external.modules.neutron;

import com.acc.local.external.dto.neutron.addressgroups.AddAddressesToGroupRequest;
import com.acc.local.external.dto.neutron.addressgroups.CreateAddressGroupRequest;
import com.acc.local.external.dto.neutron.addressgroups.RemoveAddressesFromGroupRequest;
import com.acc.local.external.dto.neutron.addressgroups.UpdateAddressGroupRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronAddressGroupsAPIModule extends NeutronAPIUtil {

    private final OpenstackAPICallModule openstackAPICallModule;

    public ResponseEntity<JsonNode> listAddressGroups(String token, Map<String, String> queryParams) {
        String uri = "/v2.0/address-groups";
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), queryParams, port);
    }

    public ResponseEntity<JsonNode> createAddressGroup(String token, CreateAddressGroupRequest request) {
        String uri = "/v2.0/address-groups";
        return openstackAPICallModule.callPostAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> showAddressGroup(String token, String addressGroupId) {
        String uri = "/v2.0/address-groups/" + addressGroupId;
        return openstackAPICallModule.callGetAPI(uri, Collections.singletonMap("X-Auth-Token", token), Collections.emptyMap(), port);
    }

    public ResponseEntity<JsonNode> updateAddressGroup(String token, String addressGroupId, UpdateAddressGroupRequest request) {
        String uri = "/v2.0/address-groups/" + addressGroupId;
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> deleteAddressGroup(String token, String addressGroupId) {
        String uri = "/v2.0/address-groups/" + addressGroupId;
        return openstackAPICallModule.callDeleteAPI(uri, Collections.singletonMap("X-Auth-Token", token), port);
    }

    public ResponseEntity<JsonNode> addAddressesToGroup(String token, String addressGroupId, AddAddressesToGroupRequest request) {
        String uri = "/v2.0/address-groups/" + addressGroupId + "/add_addresses";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }

    public ResponseEntity<JsonNode> removeAddressesFromGroup(String token, String addressGroupId, RemoveAddressesFromGroupRequest request) {
        String uri = "/v2.0/address-groups/" + addressGroupId + "/remove_addresses";
        return openstackAPICallModule.callPutAPI(uri, Collections.singletonMap("X-Auth-Token", token), request, port);
    }
}