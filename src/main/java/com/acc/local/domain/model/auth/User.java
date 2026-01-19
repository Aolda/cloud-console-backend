package com.acc.local.domain.model.auth;

import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.UserIdentityEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * User 도메인 모델
 * Module 계층에서 반환하는 완전한 사용자 정보 모델
 * Keystone, UserIdentity, UserDbExtra의 정보를 모두 포함
 */
@Getter
@Builder
public class User {

    private String userId;
    private String username;
    private String email;
    private String department;
    private String studentId;
    private String phoneNumber;
    private Boolean isEnabled;
    private Boolean isAdmin;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Keystone DTO, UserIdentity Entity, UserDbExtra Entity로부터 User 도메인 모델 생성
     * 모든 정보가 완전하게 채워진 User 객체를 반환
     */
    public static User from(UserKeystoneDto userKeystoneDto,
                           UserDbExtraEntity userDbExtra,
                           UserIdentityEntity userIdentity) {
        return User.builder()
                .userId(userKeystoneDto.id())
                .username(userDbExtra.getUserName())
                .email(userIdentity.getUserEmail())
                .department(userIdentity.getDepartment())
                .studentId(userIdentity.getStudentId())
                .phoneNumber(userDbExtra.getUserPhoneNumber())
                .isEnabled(userKeystoneDto.enabled())
                .isAdmin(userDbExtra.getIsAdmin())
                .isDeleted(userDbExtra.getIsDeleted())
                .createdAt(userDbExtra.getCreatedAt())
                .updatedAt(userDbExtra.getUpdatedAt())
                .build();
    }
}