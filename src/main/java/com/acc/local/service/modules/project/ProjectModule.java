package com.acc.local.service.modules.project;

import com.acc.local.dto.project.ProjectResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
@Component
@RequiredArgsConstructor
public class ProjectModule {
    private final WebClient keystoneWebClient;

    public List<ProjectResponse> listProjects(String token) {
        return keystoneWebClient.get()
                .uri("/identity/v3/projects")
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    List<ProjectResponse> projectResponses = new ArrayList<>();
                    json.get("projects").forEach(node ->
                            projectResponses.add(new ProjectResponse(
                                    node.get("id").asText(),
                                    node.get("name").asText()
                            ))
                    );
                    return projectResponses;
                })
                .block();
    }
}
