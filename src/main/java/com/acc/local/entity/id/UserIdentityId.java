package com.acc.local.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * 사용자 인증정보 복합키 (user_id + auth_type)
 * 동일 사용자가 여러 인증 방식을 가질 수 있도록 지원
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserIdentityId implements Serializable {

    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "auth_type")
    private Integer authType;
}