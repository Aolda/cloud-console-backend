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
public class CinderCaptureScenarios {

    private final OpenstackAPICallModule openstackAPICallModule;
    private static final int CINDER_PORT = 8776;

    public List<Scenario> build(String token, String projectId,
                                boolean volumes, boolean volumesDetail,
                                boolean snapshots, boolean backups,
                                boolean limits) {
        List<Scenario> list = new ArrayList<>();
        if (volumes) list.add(volumesScenario(token));
        if (volumesDetail) list.add(volumesDetailScenario(token));
        if (snapshots) list.add(snapshotsScenario(token, projectId));
        if (backups) list.add(backupsScenario(token));
        if (limits) list.add(limitsScenario(token));
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

    private Scenario volumesScenario(String token) {
        String uri = "/v3/volumes";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        return new BasicScenario("cinder", "volumes_list", "GET", uri, CINDER_PORT, headers, query);
    }

    private Scenario volumesDetailScenario(String token) {
        String uri = "/v3/volumes/detail";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        return new BasicScenario("cinder", "volumes_detail", "GET", uri, CINDER_PORT, headers, query);
    }

    private Scenario snapshotsScenario(String token, String projectId) {
        String base = projectId != null && !projectId.isBlank() ? "/v3/" + projectId + "/snapshots" : "/v3/snapshots";
        String uri = base;
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        return new BasicScenario("cinder", "snapshots_list", "GET", uri, CINDER_PORT, headers, query);
    }

    private Scenario backupsScenario(String token) {
        String uri = "/v3/backups";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        return new BasicScenario("cinder", "backups_list", "GET", uri, CINDER_PORT, headers, query);
    }

    private Scenario limitsScenario(String token) {
        String uri = "/v3/limits";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = Map.of();
        return new BasicScenario("cinder", "limits_get", "GET", uri, CINDER_PORT, headers, query);
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

