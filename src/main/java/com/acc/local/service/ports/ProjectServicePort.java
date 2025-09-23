package com.acc.local.service.ports;

import com.acc.local.dto.project.ProjectResponse;

import java.util.List;

public interface ProjectServicePort {
    List<ProjectResponse> listProjects(String token);

}
