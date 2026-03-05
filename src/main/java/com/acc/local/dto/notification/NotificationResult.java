package com.acc.local.dto.notification;

/**
 * 알림 채널별 전송 결과
 */
public record NotificationResult(
        boolean discordSuccess,
        boolean emailSuccess
) {

    public static NotificationResult allFailed() {
        return new NotificationResult(false, false);
    }

    public static NotificationResult ofPendingChannels(boolean needsDiscord, boolean needsEmail) {
        return new NotificationResult(!needsDiscord, !needsEmail);
    }
}
