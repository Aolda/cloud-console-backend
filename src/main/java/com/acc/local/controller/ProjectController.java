package com.acc.local.controller;

import com.acc.local.dto.project.ProjectResponse;
import com.acc.local.service.ports.ProjectServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectServicePort projectServicePort;

    @GetMapping
    public List<ProjectResponse> getProjects(@RequestHeader("X-Auth-Token") String token) {
        return projectServicePort.listProjects(token);
    }
}
