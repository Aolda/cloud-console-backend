package com.acc.local.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * 사용자-약관 동의 복합키
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserTermAgreementId implements Serializable {

    @Column(name = "term_id")
    private Long termId;

    @Column(name = "user_id", length = 64)
    private String userId;
}