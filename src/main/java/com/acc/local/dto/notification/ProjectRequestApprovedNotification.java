package com.acc.local.dto.notification;

import com.acc.local.domain.enums.notification.ProjectNotificationType;

/**
 * 프로젝트 요청 승인 알림
 */
public record ProjectRequestApprovedNotification(
        String projectRequestId,
        String projectName,
        String projectDescription,
        String requesterName,
        String requesterEmail,
        String createdProjectId
) implements ProjectNotification {

    @Override
    public ProjectNotificationType getNotificationType() {
        return ProjectNotificationType.PROJECT_REQUEST_APPROVED;
    }

    @Override
    public String getRecipientEmail() {
        return requesterEmail;
    }

    @Override
    public String getRecipientName() {
        return requesterName;
    }
}

