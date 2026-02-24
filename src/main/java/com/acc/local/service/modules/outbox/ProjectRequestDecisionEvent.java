package com.acc.local.service.modules.outbox;

import com.acc.local.domain.enums.outbox.EventType;
import com.acc.local.domain.enums.project.ProjectRequestStatus;

/**
 * 프로젝트 요청 결정 이벤트 (승인/거부)
 */
public record ProjectRequestDecisionEvent(
        String projectRequestId,
        String requesterId,
        String projectName,
        String projectDescription,
        ProjectRequestStatus decision,  // APPROVED or REJECTED
        String rejectReason,
        String createdProjectId,  // 승인 시에만 값 존재
        String email
) {
    public EventType toEventType() {
        return decision == ProjectRequestStatus.APPROVED
                ? EventType.PROJECT_REQUEST_APPROVED
                : EventType.PROJECT_REQUEST_REJECTED;
    }
}

