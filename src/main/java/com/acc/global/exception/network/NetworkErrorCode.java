package com.acc.global.exception.network;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum NetworkErrorCode implements ErrorCode {

    NOT_FOUND_ROUTER(404, "ACC-NOT-FOUND-ROUTER", "해당 라우터가 존재하지 않습니다."),
    INVALID_ROUTER_NAME(400, "ACC-INVALID-ROUTER-NAME", "라우터 이름이 유효하지 않습니다."),
    INVALID_ROUTER_GATEWAY(400, "ACC-INVALID-ROUTER-GATEWAY", "라우터 게이트웨이 설정이 유효하지 않습니다."),

    CAN_NOT_DELETE_NETWORK(403, "ACC-CAN-NOT-DELETE-NETWORK", "해당 네트워크는 삭제할 수 없습니다."),
    NOT_FOUND_NETWORK(404, "ACC-NOT-FOUND-NETWORK", "해당 네트워크가 존재하지 않습니다."),
    INVALID_NETWORK_NAME(400, "ACC-INVALID-NETWORK-NAME", "네트워크 이름이 유효하지 않습니다."),
    INVALID_NETWORK_MTU(400, "ACC-INVALID-NETWORK-MTU", "네트워크 MTU 값이 유효하지 않습니다."),

    INVALID_SUBNET_CIDR( 400, "ACC-INVALID-SUBNET-CIDR", "서브넷 CIDR 값이 유효하지 않습니다."),
    INVALID_SUBNET_NAME(400, "ACC-INVALID-SUBNET-NAME", "서브넷 이름이 유효하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;

    NetworkErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
