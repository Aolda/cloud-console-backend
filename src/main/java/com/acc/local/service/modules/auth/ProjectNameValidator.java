package com.acc.local.service.modules.auth;

import java.util.regex.Pattern;

import com.acc.global.exception.project.ProjectErrorCode;
import com.acc.global.exception.project.ProjectServiceException;

public final class ProjectNameValidator {

    private static final Pattern PROJECT_NAME_PATTERN = Pattern.compile("^[a-z0-9-]+$");

    private ProjectNameValidator() {
    }

    public static void validate(String projectName) {
        if (projectName == null || projectName.isBlank() || !PROJECT_NAME_PATTERN.matcher(projectName).matches()) {
            throw new ProjectServiceException(ProjectErrorCode.INVALID_PROJECT_NAME);
        }
    }
}
