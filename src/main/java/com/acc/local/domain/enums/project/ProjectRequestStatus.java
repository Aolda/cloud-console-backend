package com.acc.local.domain.enums.project;

import com.acc.global.exception.auth.AuthEntityException;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.project.ProjectErrorCode;
import com.acc.global.exception.project.ProjectServiceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @Override
    @JsonValue
    public String toString() {
        return code;
    }


    @JsonCreator
    public static ProjectRequestStatus fromTypeId(String code) {
        for (ProjectRequestStatus requestStatus : ProjectRequestStatus.values()) {
            if (requestStatus.code.equalsIgnoreCase(code)) {
                return requestStatus;
            }
        }

        throw new ProjectServiceException(ProjectErrorCode.INVALID_PROJECT_REQUEST_STATUS);
    }

}
