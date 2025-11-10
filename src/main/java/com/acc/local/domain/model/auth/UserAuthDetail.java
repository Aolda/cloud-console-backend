package com.acc.local.domain.model.auth;

import com.acc.local.domain.enums.auth.AuthType;
import com.acc.local.dto.auth.SignupRequest;
import com.acc.local.entity.UserAuthDetailEntity;
import com.acc.local.entity.UserDetailEntity;
import lombok.Builder;
import lombok.Getter;

/**
 * UserAuthDetail 도메인 모델
 */
@Getter
@Builder
public class UserAuthDetail {

    private String userId;
    private String department;
    private String studentId;
    private AuthType authType;
    private String userEmail;

    /**
     * SignupRequest로부터 UserAuthDetail 생성 (회원가입용)
     */
    public static UserAuthDetail createForSignup(String userId, SignupRequest request) {
        return UserAuthDetail.builder()
                .userId(userId)
                .department(request.department())
                .studentId(request.studentId())
                .authType(request.authType())
                .userEmail(request.email())
                .build();
    }

    /**
     * Entity로 변환
     * @param userDetailEntity 연관관계 설정을 위한 UserDetailEntity
     */
    public UserAuthDetailEntity toEntity(UserDetailEntity userDetailEntity) {
        return UserAuthDetailEntity.builder()
                .userId(this.userId)
                .user(userDetailEntity)
                .department(this.department)
                .studentId(this.studentId)
                .authType(this.authType.getCode())
                .userEmail(this.userEmail)
                .build();
    }

    /**
     * Entity로부터 도메인 모델 생성
     */
    public static UserAuthDetail from(UserAuthDetailEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserAuthDetail.builder()
                .userId(entity.getUserId())
                .department(entity.getDepartment())
                .studentId(entity.getStudentId())
                .authType(entity.getAuthTypeEnum())
                .userEmail(entity.getUserEmail())
                .build();
    }
}
