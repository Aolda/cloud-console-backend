package com.acc.global.exception.instance;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum InstanceErrorCode implements ErrorCode {

    INVALID_INSTANCE_NAME(400, "ACC-INSTANCE-INVALID-NAME", "인스턴스 이름이 유효하지 않습니다."),
    INVALID_ACTION(400, "ACC-INSTANCE-INVALID-ACTION", "요청한 동작(action)을 찾을 수 없습니다."),
    INVALID_PARAMETER(400, "ACC-INSTANCE-INVALID-PARAMETER", "필수 파라미터가 누락되었거나 형식이 잘못되었습니다."),

    KEYPAIR_OR_PASSWORD_REQUIRED(400, "ACC-INSTANCE-AUTH-METHOD-REQUIRED", "인증 방식은 Keypair 또는 Password 중 하나만 선택해야 합니다."),
    INVALID_KEYPAIR_NAME_FORMAT(400, "ACC-INSTANCE-INVALID-KEYPAIR-FORMAT", "키페어 이름 형식이 유효하지 않습니다."),
    INVALID_PASSWORD_POLICY(400, "ACC-INSTANCE-INVALID-PASSWORD-POLICY", "비밀번호 정책에 맞지 않습니다."),

    INVALID_FLAVOR_ID(400, "ACC-INSTANCE-INVALID-FLAVOR", "타입(Flavor) ID가 유효하지 않습니다."),
    INVALID_IMAGE_ID(400, "ACC-INSTANCE-INVALID-IMAGE", "이미지 ID가 유효하지 않거나 'active' 상태가 아닙니다."),
    INVALID_IMAGE_OR_DISK_SIZE(400, "ACC-INSTANCE-INVALID-DISK-SIZE", "볼륨 부팅이 불가능한 이미지거나, 디스크 크기가 이미지의 최소 요구 사양보다 작습니다."),

    INVALID_NETWORK_ID(400, "ACC-INSTANCE-INVALID-NETWORK", "네트워크 ID가 유효하지 않습니다."),
    INVALID_INTERFACE_ID(400, "ACC-INSTANCE-INVALID-INTERFACE", "인터페이스(Port) ID가 유효하지 않거나 'DOWN' 상태가 아닙니다."),
    INVALID_SECURITY_GROUP_ID(400, "ACC-INSTANCE-INVALID-SECURITY-GROUP", "보안 그룹 ID가 유효하지 않습니다."),

    INSTANCE_NOT_FOUND(404, "ACC-INSTANCE-NOT-FOUND", "인스턴스를 찾을 수 없습니다."),
    KEYPAIR_NOT_FOUND(404, "ACC-INSTANCE-KEYPAIR-NOT-FOUND", "존재하지 않는 키페어입니다."),

    COMPUTE_QUOTA_EXCEEDED(409, "ACC-INSTANCE-COMPUTE-QUOTA-EXCEEDED", "컴퓨트 쿼터(vCPU, RAM, 개수)가 초과되었습니다."),
    VOLUME_QUOTA_EXCEEDED(409, "ACC-INSTANCE-VOLUME-QUOTA-EXCEEDED", "볼륨 쿼터(크기, 개수)가 초과되었습니다."),

    CAN_NOT_CONTROL_INSTANCE(409, "ACC-INSTANCE-ACTION-NOT-ALLOWED", "현재 인스턴스 상태에서는 해당 동작을 수행할 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;

    InstanceErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
