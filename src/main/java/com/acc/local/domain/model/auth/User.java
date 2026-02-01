package com.acc.local.domain.model.auth;

import com.acc.global.exception.auth.AuthEntityException;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.local.domain.enums.auth.AuthType;
import com.acc.local.dto.auth.UserKeystoneDto;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.UserIdentityEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

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
     * 사용자의 모든 인증 정보 목록
     * 다중 인증 방식 (Ajou Portal, Gitlab 등)을 지원하기 위함
     */
    private List<Identity> identities;

    /**
     * 사용자 인증 정보를 담는 이너 클래스
     * UserIdentityEntity의 정보를 도메인 모델로 변환
     */
    @Getter
    @Builder
    public static class Identity {
        private AuthType authType;
        private String email;
        private String department;
        private String studentId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Identity from(UserIdentityEntity entity) {
            return Identity.builder()
                    .authType(entity.getAuthTypeEnum())
                    .email(entity.getUserEmail())
                    .department(entity.getDepartment())
                    .studentId(entity.getStudentId())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    /**
     * Keystone DTO, UserIdentity Entity, UserDbExtra Entity로부터 User 도메인 모델 생성
     * 단일 인증 정보를 사용하는 경우
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
                .identities(List.of(Identity.from(userIdentity)))
                .build();
    }

    /**
     * Keystone DTO, UserIdentity Entity 목록, UserDbExtra Entity로부터 User 도메인 모델 생성
     * 다중 인증 정보 지원(gitlab, google), 가장 최근 인증 정보가 기본 필드에 설정된다.
     */
    public static User from(UserKeystoneDto userKeystoneDto,
                           UserDbExtraEntity userDbExtra,
                           List<UserIdentityEntity> userIdentities) {
        // 가장 최근 인증 정보 조회 (createdAt 기준)
        UserIdentityEntity latestIdentity = userIdentities.stream()
                .max(Comparator.comparing(UserIdentityEntity::getCreatedAt))
                .orElseThrow(() -> new AuthEntityException(AuthErrorCode.NOT_FOUND_USER_AUTH_INFO));

        List<Identity> identityList = userIdentities.stream()
                .map(Identity::from)
                .toList();

        return User.builder()
                .userId(userKeystoneDto.id())
                .username(userDbExtra.getUserName())
                .email(latestIdentity.getUserEmail())
                .department(latestIdentity.getDepartment())
                .studentId(latestIdentity.getStudentId())
                .phoneNumber(userDbExtra.getUserPhoneNumber())
                .isEnabled(userKeystoneDto.enabled())
                .isAdmin(userDbExtra.getIsAdmin())
                .isDeleted(userDbExtra.getIsDeleted())
                .createdAt(userDbExtra.getCreatedAt())
                .updatedAt(userDbExtra.getUpdatedAt())
                .identities(identityList)
                .build();
    }
}