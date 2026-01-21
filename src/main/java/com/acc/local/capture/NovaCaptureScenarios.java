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
public class NovaCaptureScenarios {

    private final OpenstackAPICallModule openstackAPICallModule;
    private static final int NOVA_PORT = 8774;

    public List<Scenario> build(String token, String projectId,
                                boolean servers, boolean serversDetail,
                                boolean keypairs, boolean limits) {
        List<Scenario> list = new ArrayList<>();
        if (servers) list.add(serversScenario(token, projectId));
        if (serversDetail) list.add(serversDetailScenario(token, projectId));
        if (keypairs) list.add(keypairsScenario(token));
        if (limits) list.add(limitsScenario(token));
        // Additional common GETs that don't require resource-specific IDs
        list.add(flavorsScenario(token));
        list.add(flavorsDetailScenario(token));
        list.add(usageScenario(token));
        if (projectId != null && !projectId.isEmpty()) {
            list.add(quotaSetsScenario(token, projectId));
            list.add(projectUsageScenario(token, projectId));
        }
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

    private Scenario serversScenario(String token, String projectId) {
        String uri = "/v2.1/servers";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        if (projectId != null && !projectId.isEmpty()) query.put("project_id", projectId);
        return new BasicScenario("nova", "servers_list", "GET", uri, NOVA_PORT, headers, query);
    }

    private Scenario serversDetailScenario(String token, String projectId) {
        String uri = "/v2.1/servers/detail";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        if (projectId != null && !projectId.isEmpty()) query.put("project_id", projectId);
        return new BasicScenario("nova", "servers_detail", "GET", uri, NOVA_PORT, headers, query);
    }

    private Scenario keypairsScenario(String token) {
        String uri = "/v2.1/os-keypairs";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = Map.of();
        return new BasicScenario("nova", "keypairs_list", "GET", uri, NOVA_PORT, headers, query);
    }

    private Scenario limitsScenario(String token) {
        String uri = "/v2.1/limits";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = Map.of();
        return new BasicScenario("nova", "limits_get", "GET", uri, NOVA_PORT, headers, query);
    }

    private Scenario flavorsScenario(String token) {
        String uri = "/v2.1/flavors";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        return new BasicScenario("nova", "flavors_list", "GET", uri, NOVA_PORT, headers, Map.of());
    }

    private Scenario flavorsDetailScenario(String token) {
        String uri = "/v2.1/flavors/detail";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        return new BasicScenario("nova", "flavors_detail", "GET", uri, NOVA_PORT, headers, Map.of());
    }

    private Scenario quotaSetsScenario(String token, String projectId) {
        String uri = "/v2.1/os-quota-sets/" + projectId;
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        return new BasicScenario("nova", "quota_sets_get", "GET", uri, NOVA_PORT, headers, Map.of());
    }

    private Scenario usageScenario(String token) {
        String uri = "/v2.1/os-simple-tenant-usage";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        return new BasicScenario("nova", "usage_list", "GET", uri, NOVA_PORT, headers, Map.of());
    }

    private Scenario projectUsageScenario(String token, String projectId) {
        String uri = "/v2.1/os-simple-tenant-usage/" + projectId;
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        return new BasicScenario("nova", "usage_project", "GET", uri, NOVA_PORT, headers, Map.of());
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
