package com.acc.local.external.adapters.neutron;

import com.acc.global.common.PageResponse;
import com.acc.global.exception.network.NeutronErrorCode;
import com.acc.global.exception.network.NeutronException;
import com.acc.local.dto.network.ViewSecurityGroupsResponse;
import com.acc.local.external.dto.neutron.securitygroups.CreateSecurityGroupRequest;
import com.acc.local.external.modules.neutron.NeutronSecurityGroupRulesAPIModule;
import com.acc.local.external.modules.neutron.NeutronSecurityGroupsAPIModule;
import com.acc.local.external.ports.NeutronSecurityGroupExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeutronSecurityGroupExternalAdapter implements NeutronSecurityGroupExternalPort {

    private final NeutronSecurityGroupsAPIModule securityGroupsAPIModule;
    private final NeutronSecurityGroupRulesAPIModule securityRuleExternalPort;

    @Override
    public void callCreateSecurityGroup(String keystoneToken, String projectId, String securityGroupName, String description) {
        try {
            securityGroupsAPIModule.createSecurityGroup(keystoneToken,
                    CreateSecurityGroupRequest.builder().securityGroup(
                            CreateSecurityGroupRequest.SecurityGroup.builder()
                                    .name(securityGroupName)
                                    .description(description)
                                    .projectId(projectId)
                                    .build()
                    ).build());
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_GROUP_CREATION_FAILED);
        }
    }

    @Override
    public PageResponse<ViewSecurityGroupsResponse> callListSecurityGroups(String keystoneToken, String projectId, String marker, String direction, int limit) {
        try {
            ResponseEntity<JsonNode> response = securityGroupsAPIModule.listSecurityGroups(keystoneToken,
                    getListSecurityGroupsParams(projectId, marker, direction, limit > 0 ? limit + 1 : 0));

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_GROUP_RETRIEVAL_FAILED);
            }

            List<ViewSecurityGroupsResponse> securityGroups = parseSecurityGroups(response);
            return getSecurityGroupsPageResponse(marker, limit, securityGroups);
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_GROUP_RETRIEVAL_FAILED);
        }
    }

    @Override
    public ViewSecurityGroupsResponse callGetSecurityGroupById(String keystoneToken, String securityGroupId, String marker, String direction, int limit) {
        try {
            ResponseEntity<JsonNode> response = securityGroupsAPIModule.showSecurityGroup(keystoneToken, securityGroupId);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_GROUP_RETRIEVAL_FAILED);
            }

            JsonNode sgNode = response.getBody().get("security_group");
            return ViewSecurityGroupsResponse.builder()
                    .securityGroupName(sgNode.get("name").asText())
                    .description(sgNode.get("description").asText())
                    .rules(getSecurityGroupDetails(keystoneToken, securityGroupId, marker, direction, limit))
                    .build();
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_GROUP_RETRIEVAL_FAILED);
        }
    }

    @Override
    public ViewSecurityGroupsResponse callGetSecurityGroupByName(String keystoneToken, String projectId, String securityGroupName) {
        try {
            Map<String, String> params = getListSecurityGroupsParams(projectId, null, "next", 0);
            params.put("name", securityGroupName);
            ResponseEntity<JsonNode> response = securityGroupsAPIModule.listSecurityGroups(keystoneToken,
                    params);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_GROUP_RETRIEVAL_FAILED);
            }

            List<ViewSecurityGroupsResponse> securityGroups = parseSecurityGroups(response);
            if (securityGroups.size() == 1) {
                return securityGroups.getFirst();
            }
            throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_GROUP_RETRIEVAL_FAILED);

        } catch (WebClientResponseException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_GROUP_RETRIEVAL_FAILED);
        }
    }

    @Override
    public void callDeleteSecurityGroup(String keystoneToken, String securityGroupId) {
        try {
            securityGroupsAPIModule.deleteSecurityGroup(keystoneToken, securityGroupId);
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_GROUP_DELETION_FAILED);
        }
    }

    private PageResponse<ViewSecurityGroupsResponse.Rule> getSecurityGroupDetails(String keystoneToken, String securityGroupId, String marker, String direction, int limit) {
        try {
            ResponseEntity<JsonNode> response = securityRuleExternalPort.listSecurityGroupRules(keystoneToken,
                    getListSecurityGroupRulesParams(securityGroupId, marker, direction, limit > 0 ? limit + 1 : 0));
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_RETRIEVAL_FAILED);
            }

            List<ViewSecurityGroupsResponse.Rule> securityRules = parseSecurityRules(response.getBody().get("security_group_rules"));
            return getSecurityRulesPageResponse(marker, limit, securityRules);
        } catch (WebClientException e) {
            throw new NeutronException(NeutronErrorCode.NEUTRON_SECURITY_RULE_RETRIEVAL_FAILED);
        }
    }

    private PageResponse<ViewSecurityGroupsResponse.Rule> getSecurityRulesPageResponse(String marker, int limit, List<ViewSecurityGroupsResponse.Rule> securityRules) {
        int returnedSize = securityRules.size();
        if (limit != 0 && returnedSize == limit + 1) {
            securityRules.removeLast();
        }

        return PageResponse.<ViewSecurityGroupsResponse.Rule>builder()
                .contents(securityRules)
                .nextMarker(limit == 0 || returnedSize <= limit ? null : securityRules.getLast().getRuleId())
                .prevMarker(limit == 0 || marker == null ? null : securityRules.getFirst().getRuleId())
                .last(limit == 0 || returnedSize <= limit)
                .first(marker == null || limit == 0)
                .size(securityRules.size())
                .build();
    }

    private List<ViewSecurityGroupsResponse.Rule> parseSecurityRules(JsonNode securityGroupRules) {
        List<ViewSecurityGroupsResponse.Rule> rules = new ArrayList<>();

        for (JsonNode ruleNode : securityGroupRules) {
            ViewSecurityGroupsResponse.Rule rule = ViewSecurityGroupsResponse.Rule.builder()
                    .ruleId(ruleNode.get("id").asText())
                    .direction(ruleNode.get("direction").asText())
                    .protocol(ruleNode.get("protocol").isNull() ? "any" : ruleNode.get("protocol").asText())
                    .portRange(getPortRange(ruleNode))
                    .prefix(ruleNode.get("remote_ip_prefix").isNull() ? null : ruleNode.get("remote_ip_prefix").asText())
                    .build();
            rules.add(rule);
        }

        return rules;
    }

    private Map<String, String> getListSecurityGroupRulesParams(String securityGroupId, String marker, String direction, int limit) {
        Map<String, String> params = new HashMap<>(Map.of(
                "security_group_id", securityGroupId,
                "page_reverse", direction.equals("prev") ? "true" : "false",
                "limit", String.valueOf(limit)
        ));

        if (marker != null && !marker.isEmpty()) {
            params.put("marker", marker);
        }

        return params;
    }

    private String getPortRange(JsonNode ruleNode) {
        if (ruleNode.get("port_range_min").isNull() || ruleNode.get("port_range_max").isNull()) {
            return "any";
        }
        int portMin = ruleNode.get("port_range_min").asInt();
        int portMax = ruleNode.get("port_range_max").asInt();
        return portMin == portMax ? String.valueOf(portMin) : portMin + ":" + portMax;
    }


    private PageResponse<ViewSecurityGroupsResponse> getSecurityGroupsPageResponse(String marker, int limit, List<ViewSecurityGroupsResponse> securityGroups) {
        int returnedSize = securityGroups.size();
        if (limit != 0 && returnedSize == limit + 1) {
            securityGroups.removeLast();
        }

        return PageResponse.<ViewSecurityGroupsResponse>builder()
                .contents(securityGroups)
                .nextMarker(limit == 0 || returnedSize <= limit ? null : securityGroups.getLast().getSecurityGroupId())
                .prevMarker(limit == 0 || marker == null ? null : securityGroups.getFirst().getSecurityGroupId())
                .last(limit == 0 || returnedSize <= limit)
                .first(marker == null || limit == 0)
                .size(securityGroups.size())
                .build();
    }

    private List<ViewSecurityGroupsResponse> parseSecurityGroups(ResponseEntity<JsonNode> response) {
        List<ViewSecurityGroupsResponse> securityGroups = new java.util.ArrayList<>();

        for (JsonNode sgNode : response.getBody().get("security_groups")) {
            ViewSecurityGroupsResponse sg = ViewSecurityGroupsResponse.builder()
                    .securityGroupId(sgNode.get("id").asText())
                    .securityGroupName(sgNode.get("name").asText())
                    .description(sgNode.get("description").asText())
                    .createdAt(sgNode.get("created_at").asText())
                    .build();

            securityGroups.add(sg);
        }

        return securityGroups;
    }

    private Map<String, String> getListSecurityGroupsParams(String projectId, String marker, String direction, int limit) {
        Map<String, String> params = new HashMap<>(Map.of(
                "project_id", projectId,
                "page_reverse", direction.equals("prev") ? "true" : "false",
                "limit", String.valueOf(limit)
        ));

        if (marker != null && !marker.isEmpty()) {
            params.put("marker", marker);
        }

        return params;
    }

}
