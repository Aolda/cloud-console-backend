package com.acc.local.dto.project;

import com.acc.local.domain.enums.project.ProjectRequestType;
import com.acc.local.dto.project.quota.ProjectQuotaRequest;

import lombok.Builder;

@Builder
public record ProjectCreateDto(
        String projectName,
        String projectDescription,
        ProjectRequestType projectType,
        ProjectQuotaRequest quota,
        String projectOwnerId
) {

}
