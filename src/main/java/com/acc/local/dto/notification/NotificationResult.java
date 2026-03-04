package com.acc.local.dto.notification;

/**
 * 알림 채널별 전송 결과
 * Discord, Email 각각의 성공 여부를 독립적으로 추적합니다.
 */
public record NotificationResult(
        boolean discordSuccess,
        boolean emailSuccess
) {

    public static NotificationResult allSuccess() {
        return new NotificationResult(true, true);
    }

    public static NotificationResult allFailed() {
        return new NotificationResult(false, false);
    }

    public boolean isFullyProcessed() {
        return discordSuccess && emailSuccess;
    }
}

