package com.acc.local.service.adapters.project;

import com.acc.local.service.modules.project.ProjectModule;
import com.acc.local.service.ports.ProjectPort;
import com.acc.local.dto.project.ProjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@Primary
@RequiredArgsConstructor
public class ProjectAdapter implements ProjectPort {
    private final ProjectModule projectModule;

    @Override
    public List<ProjectResponse> listProjects(String token) {
        return projectModule.listProjects(token);
    }
}
