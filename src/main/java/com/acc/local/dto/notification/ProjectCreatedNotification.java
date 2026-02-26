package com.acc.local.dto.notification;

import com.acc.local.domain.enums.notification.ProjectNotificationType;

/**
 * 프로젝트 직접 생성 알림 (관리자가 직접 생성)
 */
public record ProjectCreatedNotification(
        String projectId,
        String projectName,
        String ownerName,
        String ownerEmail
) implements ProjectNotification {

    @Override
    public ProjectNotificationType getNotificationType() {
        return ProjectNotificationType.PROJECT_CREATED;
    }

    @Override
    public String getRecipientEmail() {
        return ownerEmail;
    }

    @Override
    public String getRecipientName() {
        return ownerName;
    }
}

