package com.acc.local.service.modules.outbox;

import com.acc.local.domain.enums.project.ProjectRequestType;

public record ProjectRequestEvent (
        String projectRequestId,
        String requesterId,
        String projectName,
        ProjectRequestType projectType,
        String projectDescription,
        String email
) {}
