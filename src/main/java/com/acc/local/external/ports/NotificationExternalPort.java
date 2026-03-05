package com.acc.local.external.ports;

import com.acc.local.dto.notification.NotificationResult;
import com.acc.local.dto.notification.ProjectNotification;

public interface NotificationExternalPort {

    NotificationResult sendProjectNotification(ProjectNotification notification, NotificationResult previousResult);
}
