package com.acc.local.dto.notification;

import com.acc.local.domain.enums.notification.ProjectNotificationType;

/**
 * 프로젝트 알림 공통 인터페이스
 */
public interface ProjectNotification {

    ProjectNotificationType getNotificationType();
    String getRecipientEmail();
    String getRecipientName();
}

