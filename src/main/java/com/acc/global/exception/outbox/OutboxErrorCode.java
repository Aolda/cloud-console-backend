package com.acc.global.exception.outbox;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

/**
 * Outbox 에러코드
 */
@Getter
public enum OutboxErrorCode implements ErrorCode {

    // 422 Unprocessable Entity - 복구 불가 에러 (재시도해도 소용없는 케이스)
    PAYLOAD_DESERIALIZATION_FAILED(422, "ACC-OUTBOX-PAYLOAD-DESERIALIZATION-FAILED", "Outbox 이벤트 페이로드 역직렬화에 실패했습니다."),
    UNKNOWN_AGGREGATE_TYPE(422, "ACC-OUTBOX-UNKNOWN-AGGREGATE-TYPE", "알 수 없는 Aggregate 타입입니다."),
    UNKNOWN_EVENT_TYPE(422, "ACC-OUTBOX-UNKNOWN-EVENT-TYPE", "알 수 없는 Event 타입입니다."),

    // 500 Internal Server Error - 일시적 오류 (재시도 가능)
    NOTIFICATION_SEND_FAILED(500, "ACC-OUTBOX-NOTIFICATION-SEND-FAILED", "알림 전송에 실패했습니다."),
    EVENT_PROCESSING_FAILED(500, "ACC-OUTBOX-EVENT-PROCESSING-FAILED", "Outbox 이벤트 처리 중 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;

    OutboxErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

