package com.acc.local.domain.enums.project;

import com.acc.global.exception.auth.AuthEntityException;
import com.acc.global.exception.auth.AuthErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 프로젝트 생성 요청 처리 상태
 */
@Getter
@RequiredArgsConstructor
public enum ProjectRequestStatus {
    PENDING("PENDING", "대기중"),
    APPROVED("APPROVED", "승인됨"),
    REJECTED("REJECTED", "반려됨"),
    COMPLETED("COMPLETED", "완료됨");

    private final String code;
    private final String description;

    public static ProjectRequestStatus fromCode(String code) {
        for (ProjectRequestStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new AuthEntityException(AuthErrorCode.INVALID_PROJECT_STATUS, "잘못된 프로젝트 상태값 입니다.");
    }
}
