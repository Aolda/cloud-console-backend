package com.acc.local.entity;

import com.acc.local.domain.enums.auth.AuthType;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 인증정보 엔티티
 */
@Entity
@Table(name = "user_auth_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAuthDetailEntity {

    @Id
    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserDetailEntity user;

    @Column(name = "department", length = 100, nullable = false)
    private String department;

    @Column(name = "student_id", length = 32, nullable = false)
    private String studentId;

    @Column(name = "auth_type", nullable = false)
    private Integer authType;

    // Enum 변환 헬퍼 메서드
    public AuthType getAuthTypeEnum() {
        return AuthType.fromCode(this.authType);
    }

    public void setAuthTypeEnum(AuthType authType) {
        this.authType = authType.getCode();
    }
}