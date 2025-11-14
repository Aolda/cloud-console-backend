package com.acc.local.domain.model.auth;

import com.acc.local.dto.auth.SignupRequest;
import com.acc.local.entity.UserDetailEntity;
import lombok.Builder;
import lombok.Getter;

/**
 * UserDetail 도메인 모델
 */
@Getter
@Builder
public class UserDetail {

    private String userId;
    private String userName;
    private String userPhoneNumber;
    private Boolean isAdmin;

    /**
     * SignupRequest로부터 UserDetail 생성 (회원가입용)
     */
    public static UserDetail createForSignup(String userId, SignupRequest request) {
        return UserDetail.builder()
                .userId(userId)
                .userName(request.username())
                .userPhoneNumber(request.phoneNumber())
                .isAdmin(false) // 회원가입은 항상 일반 사용자
                .build();
    }
    /**
     * Entity로 변환
     */
    public UserDetailEntity toEntity() {
        return UserDetailEntity.builder()
                .userId(this.userId)
                .userName(this.userName)
                .userPhoneNumber(this.userPhoneNumber)
                .isAdmin(this.isAdmin)
                .build();
    }

    /**
     * Entity로부터 도메인 모델 생성
     */
    public static UserDetail from(UserDetailEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserDetail.builder()
                .userId(entity.getUserId())
                .userName(entity.getUserName())
                .userPhoneNumber(entity.getUserPhoneNumber())
                .isAdmin(entity.getIsAdmin())
                .build();
    }
}
