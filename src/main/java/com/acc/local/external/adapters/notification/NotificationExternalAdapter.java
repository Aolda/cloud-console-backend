package com.acc.local.external.adapters.notification;

import com.acc.local.dto.notification.*;
import com.acc.local.external.modules.discord.DiscordWebhookModule;
import com.acc.local.external.modules.email.EmailNotificationModule;
import com.acc.local.external.ports.NotificationExternalPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationExternalAdapter implements NotificationExternalPort {

    private final DiscordWebhookModule discordWebhookModule;
    private final EmailNotificationModule emailNotificationModule;

    @Override
    public NotificationResult sendProjectNotification(ProjectNotification notification, NotificationResult previousResult) {
        return switch (notification) {
            case ProjectRequestCreatedNotification created -> sendProjectRequestCreatedNotification(created, previousResult);
            case ProjectRequestApprovedNotification approved -> sendProjectRequestApprovedNotification(approved, previousResult);
            case ProjectRequestRejectedNotification rejected -> sendProjectRequestRejectedNotification(rejected, previousResult);
            case ProjectCreatedNotification projectCreated -> sendProjectCreatedNotification(projectCreated, previousResult);
            default -> {
                log.warn("Unknown notification type: {}", notification.getClass().getSimpleName());
                yield NotificationResult.allFailed();
            }
        };
    }

    /**
     * 프로젝트 요청 생성 알림
     */
    private NotificationResult sendProjectRequestCreatedNotification(ProjectRequestCreatedNotification notification, NotificationResult previousResult) {
        boolean discordSuccess = previousResult.discordSuccess();
        boolean emailSuccess = previousResult.emailSuccess();

        if (!discordSuccess) {
            try {
                discordWebhookModule.sendProjectRequestCreatedNotification(
                        notification.requesterName(),
                        notification.requesterEmail(),
                        notification.projectName(),
                        notification.projectRequestId(),
                        notification.projectDescription()
                );
                log.info("Discord notification sent for PROJECT_REQUEST_CREATED: projectRequestId={}", notification.projectRequestId());
                discordSuccess = true;
            } catch (Exception e) {
                log.error("Failed to send Discord notification for PROJECT_REQUEST_CREATED: projectRequestId={}", notification.projectRequestId(), e);
            }
        } else {
            log.debug("Discord notification skipped (already SUCCESS) for PROJECT_REQUEST_CREATED: projectRequestId={}", notification.projectRequestId());
        }

        if (!emailSuccess) {
            try {
                emailNotificationModule.sendProjectRequestCreatedNotification(
                        notification.requesterName(),
                        notification.requesterEmail(),
                        notification.projectName(),
                        notification.projectRequestId(),
                        notification.projectDescription()
                );
                log.info("Email notification sent for PROJECT_REQUEST_CREATED: projectRequestId={}", notification.projectRequestId());
                emailSuccess = true;
            } catch (Exception e) {
                log.error("Failed to send Email notification for PROJECT_REQUEST_CREATED: projectRequestId={}", notification.projectRequestId(), e);
            }
        } else {
            log.debug("Email notification skipped (already SUCCESS) for PROJECT_REQUEST_CREATED: projectRequestId={}", notification.projectRequestId());
        }

        return new NotificationResult(discordSuccess, emailSuccess);
    }

    /**
     * 프로젝트 요청 승인 알림
     */
    private NotificationResult sendProjectRequestApprovedNotification(ProjectRequestApprovedNotification notification, NotificationResult previousResult) {
        boolean discordSuccess = previousResult.discordSuccess();
        boolean emailSuccess = previousResult.emailSuccess();

        if (!discordSuccess) {
            try {
                discordWebhookModule.sendProjectApprovalNotification(
                        notification.requesterName(),
                        notification.requesterEmail(),
                        notification.projectName(),
                        notification.projectRequestId(),
                        notification.projectDescription(),
                        notification.createdProjectId()
                );
                log.info("Discord notification sent for PROJECT_REQUEST_APPROVED: projectRequestId={}, createdProjectId={}", notification.projectRequestId(), notification.createdProjectId());
                discordSuccess = true;
            } catch (Exception e) {
                log.error("Failed to send Discord notification for PROJECT_REQUEST_APPROVED: projectRequestId={}", notification.projectRequestId(), e);
            }
        } else {
            log.debug("Discord notification skipped (already SUCCESS) for PROJECT_REQUEST_APPROVED: projectRequestId={}", notification.projectRequestId());
        }

        if (!emailSuccess) {
            try {
                emailNotificationModule.sendProjectApprovalNotification(
                        notification.requesterName(),
                        notification.requesterEmail(),
                        notification.projectName(),
                        notification.createdProjectId()
                );
                log.info("Email notification sent for PROJECT_REQUEST_APPROVED: projectRequestId={}", notification.projectRequestId());
                emailSuccess = true;
            } catch (Exception e) {
                log.error("Failed to send Email notification for PROJECT_REQUEST_APPROVED: projectRequestId={}", notification.projectRequestId(), e);
            }
        } else {
            log.debug("Email notification skipped (already SUCCESS) for PROJECT_REQUEST_APPROVED: projectRequestId={}", notification.projectRequestId());
        }

        return new NotificationResult(discordSuccess, emailSuccess);
    }

    /**
     * 프로젝트 요청 거부 알림
     */
    private NotificationResult sendProjectRequestRejectedNotification(ProjectRequestRejectedNotification notification, NotificationResult previousResult) {
        boolean discordSuccess = previousResult.discordSuccess();
        boolean emailSuccess = previousResult.emailSuccess();

        if (!discordSuccess) {
            try {
                discordWebhookModule.sendProjectRejectionNotification(
                        notification.requesterName(),
                        notification.requesterEmail(),
                        notification.projectName(),
                        notification.projectRequestId(),
                        notification.projectDescription(),
                        notification.rejectReason()
                );
                log.info("Discord notification sent for PROJECT_REQUEST_REJECTED: projectRequestId={}, rejectReason={}", notification.projectRequestId(), notification.rejectReason());
                discordSuccess = true;
            } catch (Exception e) {
                log.error("Failed to send Discord notification for PROJECT_REQUEST_REJECTED: projectRequestId={}", notification.projectRequestId(), e);
            }
        } else {
            log.debug("Discord notification skipped (already SUCCESS) for PROJECT_REQUEST_REJECTED: projectRequestId={}", notification.projectRequestId());
        }

        if (!emailSuccess) {
            try {
                emailNotificationModule.sendProjectRejectionNotification(
                        notification.requesterName(),
                        notification.requesterEmail(),
                        notification.projectName(),
                        notification.rejectReason()
                );
                log.info("Email notification sent for PROJECT_REQUEST_REJECTED: projectRequestId={}", notification.projectRequestId());
                emailSuccess = true;
            } catch (Exception e) {
                log.error("Failed to send Email notification for PROJECT_REQUEST_REJECTED: projectRequestId={}", notification.projectRequestId(), e);
            }
        } else {
            log.debug("Email notification skipped (already SUCCESS) for PROJECT_REQUEST_REJECTED: projectRequestId={}", notification.projectRequestId());
        }

        return new NotificationResult(discordSuccess, emailSuccess);
    }

    /**
     * 프로젝트 생성 알림
     */
    private NotificationResult sendProjectCreatedNotification(ProjectCreatedNotification notification, NotificationResult previousResult) {
        boolean discordSuccess = previousResult.discordSuccess();
        boolean emailSuccess = previousResult.emailSuccess();

        if (!discordSuccess) {
            try {
                discordWebhookModule.sendProjectDirectlyCreatedNotification(
                        notification.projectId(),
                        notification.projectName(),
                        notification.ownerName()
                );
                log.info("Discord notification sent for PROJECT_CREATED: projectId={}, projectName={}", notification.projectId(), notification.projectName());
                discordSuccess = true;
            } catch (Exception e) {
                log.error("Failed to send Discord notification for PROJECT_CREATED: projectId={}", notification.projectId(), e);
            }
        } else {
            log.debug("Discord notification skipped (already SUCCESS) for PROJECT_CREATED: projectId={}", notification.projectId());
        }

        if (!emailSuccess) {
            try {
                emailNotificationModule.sendProjectDirectlyCreatedNotification(
                        notification.projectId(),
                        notification.projectName(),
                        notification.ownerName(),
                        notification.ownerEmail()
                );
                log.info("Email notification sent for PROJECT_CREATED: projectId={}", notification.projectId());
                emailSuccess = true;
            } catch (Exception e) {
                log.error("Failed to send Email notification for PROJECT_CREATED: projectId={}", notification.projectId(), e);
            }
        } else {
            log.debug("Email notification skipped (already SUCCESS) for PROJECT_CREATED: projectId={}", notification.projectId());
        }

        return new NotificationResult(discordSuccess, emailSuccess);
    }
}
