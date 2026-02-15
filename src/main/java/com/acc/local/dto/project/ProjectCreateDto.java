package com.acc.local.dto.project;

import com.acc.local.dto.project.quota.ProjectQuotaRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ProjectCreateDto(
        String projectName,
        String projectDescription,
        ProjectQuotaRequest quota,
        String projectOwnerId
) {

}
