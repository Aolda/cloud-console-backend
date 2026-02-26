package com.acc.local.dto.notification;

import com.acc.local.domain.enums.notification.ProjectNotificationType;

/**
 * 프로젝트 요청 생성 알림
 */
public record ProjectRequestCreatedNotification(
        String projectRequestId,
        String projectName,
        String projectDescription,
        String requesterName,
        String requesterEmail
) implements ProjectNotification {

    @Override
    public ProjectNotificationType getNotificationType() {
        return ProjectNotificationType.PROJECT_REQUEST_CREATED;
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

