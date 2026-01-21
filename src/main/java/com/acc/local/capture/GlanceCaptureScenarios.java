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
public class GlanceCaptureScenarios {

    private final OpenstackAPICallModule openstackAPICallModule;
    private static final int GLANCE_PORT = 9292;

    public List<Scenario> build(String token, boolean images, boolean tasks) {
        List<Scenario> list = new ArrayList<>();
        if (images) list.add(imagesScenario(token));
        if (tasks) list.add(imagesTasksScenario(token));
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

    private Scenario imagesScenario(String token) {
        String uri = "/v2/images";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = new HashMap<>();
        return new BasicScenario("glance", "images_list", "GET", uri, GLANCE_PORT, headers, query);
    }

    private Scenario imagesTasksScenario(String token) {
        String uri = "/v2/tasks";
        Map<String, String> headers = Map.of("X-Auth-Token", token);
        Map<String, String> query = Map.of();
        return new BasicScenario("glance", "tasks_list", "GET", uri, GLANCE_PORT, headers, query);
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

