package com.acc.global.exception.instance;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum InstanceErrorCode implements ErrorCode {

    INVALID_INSTANCE_NAME(400, "ACC-INSTANCE-INVALID-NAME", "인스턴스 이름이 유효하지 않습니다."),
    INVALID_ACTION(400, "ACC-INSTANCE-INVALID-ACTION", "요청한 동작(action)을 찾을 수 없습니다."),
    INVALID_PARAMETER(400, "ACC-INSTANCE-INVALID-PARAMETER", "필수 파라미터가 누락되었거나 형식이 잘못되었습니다."),

    KEYPAIR_OR_PASSWORD_REQUIRED(400, "ACC-INSTANCE-AUTH-METHOD-REQUIRED", "인증 방식은 Keypair 또는 Password 중 하나만 선택해야 합니다."),
    NETWORK_OR_INTERFACE_REQUIRED(400, "ACC-INSTANCE-NETWORK-REQUIRED", "네트워크 ID 또는 인터페이스 ID 중 최소 1개가 필요합니다."),

    COMPUTE_QUOTA_EXCEEDED(403, "ACC-INSTANCE-COMPUTE-QUOTA-EXCEEDED", "컴퓨트 쿼터(vCPU, RAM, 개수)가 초과되었습니다.");

    private final int status;
    private final String code;
    private final String message;

    InstanceErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
