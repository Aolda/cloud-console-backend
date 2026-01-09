package com.acc.local.capture;

import com.acc.local.external.modules.OpenstackAPICallModule;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NeutronCaptureScenarios {

    private final OpenstackAPICallModule openstackAPICallModule;

    private static final int NEUTRON_PORT = 9696;

    public List<Scenario> build(String token, String projectId, boolean networks, boolean ports,
                                boolean securityGroups, boolean securityRules, boolean subnets,
                                boolean routers, boolean floatingIps) {
        List<Scenario> list = new ArrayList<>();
        if (networks) list.add(networksScenario(token, projectId));
        if (ports) list.add(portsScenario(token, projectId));
        if (securityGroups) list.add(securityGroupsScenario(token, projectId));
        if (securityRules) list.add(securityGroupRulesScenario(token));
        if (subnets) list.add(subnetsScenario(token, projectId));
        if (routers) list.add(routersScenario(token, projectId));
        if (floatingIps) list.add(floatingIpsScenario(token));
        return list;
    }

    public interface Scenario {
        String component();
        String name();
        String method();
        String uri();
        int port();
        Map<String, String> headers();
        Map<String, String> query();
        ResponseEntity<JsonNode> execute();
    }

    private Scenario networksScenario(String token, String projectId) {
        String uri = "/v2.0/networks";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        if (projectId != null && !projectId.isEmpty()) query.put("project_id", projectId);
        return new BasicScenario("neutron", "networks_list", "GET", uri, NEUTRON_PORT, headers, query);
    }

    private Scenario portsScenario(String token, String projectId) {
        String uri = "/v2.0/ports";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        if (projectId != null && !projectId.isEmpty()) query.put("project_id", projectId);
        return new BasicScenario("neutron", "ports_list", "GET", uri, NEUTRON_PORT, headers, query);
    }

    private Scenario securityGroupsScenario(String token, String projectId) {
        String uri = "/v2.0/security-groups";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        if (projectId != null && !projectId.isEmpty()) query.put("project_id", projectId);
        return new BasicScenario("neutron", "security_groups_list", "GET", uri, NEUTRON_PORT, headers, query);
    }

    private Scenario securityGroupRulesScenario(String token) {
        String uri = "/v2.0/security-group-rules";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = Map.of();
        return new BasicScenario("neutron", "security_group_rules_list", "GET", uri, NEUTRON_PORT, headers, query);
    }

    private Scenario subnetsScenario(String token, String projectId) {
        String uri = "/v2.0/subnets";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        if (projectId != null && !projectId.isEmpty()) query.put("project_id", projectId);
        return new BasicScenario("neutron", "subnets_list", "GET", uri, NEUTRON_PORT, headers, query);
    }

    private Scenario routersScenario(String token, String projectId) {
        String uri = "/v2.0/routers";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        if (projectId != null && !projectId.isEmpty()) query.put("project_id", projectId);
        return new BasicScenario("neutron", "routers_list", "GET", uri, NEUTRON_PORT, headers, query);
    }

    private Scenario floatingIpsScenario(String token) {
        String uri = "/v2.0/floatingips";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = Map.of();
        return new BasicScenario("neutron", "floatingips_list", "GET", uri, NEUTRON_PORT, headers, query);
    }

    @RequiredArgsConstructor
    private class BasicScenario implements Scenario {
        private final String component;
        private final String name;
        private final String method;
        private final String uri;
        private final int port;
        private final Map<String, String> headers;
        private final Map<String, String> query;

        @Override public String component() { return component; }
        @Override public String name() { return name; }
        @Override public String method() { return method; }
        @Override public String uri() { return uri; }
        @Override public int port() { return port; }
        @Override public Map<String, String> headers() { return headers; }
        @Override public Map<String, String> query() { return query; }

        @Override
        public ResponseEntity<JsonNode> execute() {
            return openstackAPICallModule.callGetAPI(uri, headers, query, port);
        }
    }
}

