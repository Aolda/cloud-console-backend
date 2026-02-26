package com.acc.local.service.adapters.notification;

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
public class NotificationAdapter implements NotificationExternalPort {

    private final DiscordWebhookModule discordWebhookModule;
    private final EmailNotificationModule emailNotificationModule;

    @Override
    public void sendProjectNotification(ProjectNotification notification) {
        switch (notification) {
            case ProjectRequestCreatedNotification created -> sendProjectRequestCreatedNotification(created);
            case ProjectRequestApprovedNotification approved -> sendProjectRequestApprovedNotification(approved);
            case ProjectRequestRejectedNotification rejected -> sendProjectRequestRejectedNotification(rejected);
            case ProjectCreatedNotification projectCreated -> sendProjectCreatedNotification(projectCreated);
            default -> log.warn("Unknown notification type: {}", notification.getClass().getSimpleName());
        }
    }

    /**
     * 프로젝트 요청 생성 알림
     */
    private void sendProjectRequestCreatedNotification(ProjectRequestCreatedNotification notification) {
        // Discord 알림 전송
        try {
            discordWebhookModule.sendProjectRequestCreatedNotification(
                    notification.requesterName(),
                    notification.requesterEmail(),
                    notification.projectName(),
                    notification.projectRequestId(),
                    notification.projectDescription()
            );
            log.info("Discord notification sent for PROJECT_REQUEST_CREATED: projectRequestId={}", notification.projectRequestId());
        } catch (Exception e) {
            log.error("Failed to send Discord notification for PROJECT_REQUEST_CREATED: projectRequestId={}", notification.projectRequestId(), e);
        }

        // Email 알림 전송
        try {
            emailNotificationModule.sendProjectRequestCreatedNotification(
                    notification.requesterName(),
                    notification.requesterEmail(),
                    notification.projectName(),
                    notification.projectRequestId(),
                    notification.projectDescription()
            );
            log.info("Email notification sent for PROJECT_REQUEST_CREATED: projectRequestId={}", notification.projectRequestId());
        } catch (Exception e) {
            log.error("Failed to send Email notification for PROJECT_REQUEST_CREATED: projectRequestId={}", notification.projectRequestId(), e);
        }
    }

    /**
     * 프로젝트 요청 승인 알림
     */
    private void sendProjectRequestApprovedNotification(ProjectRequestApprovedNotification notification) {
        // Discord 알림 전송
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
        } catch (Exception e) {
            log.error("Failed to send Discord notification for PROJECT_REQUEST_APPROVED: projectRequestId={}", notification.projectRequestId(), e);
        }

        // Email 알림 전송
        try {
            emailNotificationModule.sendProjectApprovalNotification(
                    notification.requesterName(),
                    notification.requesterEmail(),
                    notification.projectName(),
                    notification.createdProjectId()
            );
            log.info("Email notification sent for PROJECT_REQUEST_APPROVED: projectRequestId={}", notification.projectRequestId());
        } catch (Exception e) {
            log.error("Failed to send Email notification for PROJECT_REQUEST_APPROVED: projectRequestId={}", notification.projectRequestId(), e);
        }
    }

    /**
     * 프로젝트 요청 거부 알림
     */
    private void sendProjectRequestRejectedNotification(ProjectRequestRejectedNotification notification) {
        // Discord 알림 전송
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
        } catch (Exception e) {
            log.error("Failed to send Discord notification for PROJECT_REQUEST_REJECTED: projectRequestId={}", notification.projectRequestId(), e);
        }

        // Email 알림 전송
        try {
            emailNotificationModule.sendProjectRejectionNotification(
                    notification.requesterName(),
                    notification.requesterEmail(),
                    notification.projectName(),
                    notification.rejectReason()
            );
            log.info("Email notification sent for PROJECT_REQUEST_REJECTED: projectRequestId={}", notification.projectRequestId());
        } catch (Exception e) {
            log.error("Failed to send Email notification for PROJECT_REQUEST_REJECTED: projectRequestId={}", notification.projectRequestId(), e);
        }
    }

    /**
     * 프로젝트 생성 알림
     */
    private void sendProjectCreatedNotification(ProjectCreatedNotification notification) {
        // Discord 알림 전송
        try {
            discordWebhookModule.sendProjectDirectlyCreatedNotification(
                    notification.projectId(),
                    notification.projectName(),
                    notification.ownerName()
            );
            log.info("Discord notification sent for PROJECT_CREATED: projectId={}, projectName={}", notification.projectId(), notification.projectName());
        } catch (Exception e) {
            log.error("Failed to send Discord notification for PROJECT_CREATED: projectId={}", notification.projectId(), e);
        }

        // Email 알림 전송
        try {
            emailNotificationModule.sendProjectDirectlyCreatedNotification(
                    notification.projectId(),
                    notification.projectName(),
                    notification.ownerName(),
                    notification.ownerEmail()
            );
            log.info("Email notification sent for PROJECT_CREATED: projectId={}", notification.projectId());
        } catch (Exception e) {
            log.error("Failed to send Email notification for PROJECT_CREATED: projectId={}", notification.projectId(), e);
        }
    }
}

