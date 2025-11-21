package com.acc.local.domain.model.auth;

import com.acc.local.dto.auth.CreateNoticeRequest;
import com.acc.local.entity.NoticeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Notice {

    private String noticeId;
    private String noticeUserId;
    private String noticeTitle;
    private String noticeDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    public static NoticeEntity toEntity(Notice notice) {
        return NoticeEntity.builder()
                .noticeId(notice.getNoticeId())
                .noticeUserId(notice.getNoticeUserId())
                .noticeTitle(notice.getNoticeTitle())
                .noticeDescription(notice.getNoticeDescription())
                .startsAt(notice.getStartsAt())
                .endsAt(notice.getEndsAt())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    public static Notice from(NoticeEntity entity) {
        return Notice.builder()
                .noticeId(entity.getNoticeId())
                .noticeDescription(entity.getNoticeDescription())
                .noticeUserId(entity.getNoticeUserId())
                .noticeTitle(entity.getNoticeTitle())
                .noticeDescription(entity.getNoticeDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .startsAt(entity.getStartsAt())
                .endsAt(entity.getEndsAt())
                .build();
    }

    public static Notice create(CreateNoticeRequest request ,String creatorId) {
        return  Notice.builder()
                .noticeId(generateNoticeId())
                .noticeTitle(request.title())
                .noticeDescription(request.content())
                .noticeUserId(creatorId)
                .endsAt(LocalDateTime.parse(request.endsAt()))
                .startsAt(LocalDateTime.parse(request.startsAt()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 공지사항 ID 생성 (SHA-256 해시)
     */
    private static String generateNoticeId() {
        String source = UUID.randomUUID().toString() + System.currentTimeMillis();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(source.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
