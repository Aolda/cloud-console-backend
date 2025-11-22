package com.acc.global.exception.network;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum NetworkErrorCode implements ErrorCode {

    INVALID_SECURITY_GROUP_NAME(400, "ACC-NETWORK-INVALID-SECURITY-GROUP-NAME", "보안 그룹 이름이 유효하지 않습니다."),
    
    NOT_FOUND_SSH_FORWARDING(404, "ACC-NETWORK-NOT-FOUND-SSH-FORWARDING", "해당 포트포워딩이 존재하지 않습니다."),

    EXTERNAL_IP_ALLOCATION_FAILED(400, "ACC-NETWORK-EXTERNAL-IP-ALLOCATION-FAILED", "외부 네트워크 연결을 위한 외부 IP 할당에 실패했습니다."),
    HAS_NOT_EXTERNAL_IP(400, "ACC-NETWORK-HAS-NOT-EXTERNAL-IP", "해당 인터페이스에 외부 IP가 할당되어 있지 않습니다."),
    ALREADY_HAS_EXTERNAL_IP(400, "ACC-NETWORK-ALREADY-HAS-EXTERNAL-IP", "해당 인터페이스에 이미 외부 IP가 할당되어 있습니다."),

    NOT_FOUND_INTERFACE(404, "ACC-NETWORK-NOT-FOUND-INTERFACE", "해당 인터페이스가 존재하지 않습니다."),
    INVALID_INTERFACE_NAME(400, "ACC-NETWORK-INVALID-INTERFACE-NAME", "인터페이스 이름이 유효하지 않습니다."),
    NOT_NULL_INTERFACE_NETWORK_ID(400, "ACC-NETWORK-NOT-NULL-INTERFACE-NETWORK-ID", "인터페이스의 네트워크 ID는 null이 될 수 없습니다."),
    NOT_NULL_INTERFACE_SECURITY_GROUP_IDS(400, "ACC-NETWORK-NOT-NULL-INTERFACE-SECURITY-GROUP-IDS", "인터페이스의 시큐리티 그룹 ID는 null이 될 수 없습니다."),
    NOT_NULL_INTERFACE_EXTERNAL(400, "ACC-NETWORK-NOT-NULL-INTERFACE-EXTERNAL", "인터페이스의 외부 네트워크 연결 여부는 null이 될 수 없습니다."),
    NOT_NULL_INTERFACE_ID(400, "ACC-NETWORK-NOT-NULL-INTERFACE-ID", "인터페이스 ID는 null이 될 수 없습니다."),

    NOT_FOUND_ROUTER(404, "ACC-NETWORK-NOT-FOUND-ROUTER", "해당 라우터가 존재하지 않습니다."),
    INVALID_ROUTER_NAME(400, "ACC-NETWORK-INVALID-ROUTER-NAME", "라우터 이름이 유효하지 않습니다."),
    INVALID_ROUTER_GATEWAY(400, "ACC-NETWORK-INVALID-ROUTER-GATEWAY", "라우터 게이트웨이 설정이 유효하지 않습니다."),
    CAN_NOT_DELETE_ROUTER(403, "ACC-NETWORK-CAN-NOT-DELETE-ROUTER", "해당 라우터는 삭제할 수 없습니다."),

    CAN_NOT_DELETE_NETWORK(403, "ACC-NETWORK-CAN-NOT-DELETE-NETWORK", "해당 네트워크는 삭제할 수 없습니다."),
    NOT_FOUND_NETWORK(404, "ACC-NETWORK-NOT-FOUND-NETWORK", "해당 네트워크가 존재하지 않습니다."),
    INVALID_NETWORK_NAME(400, "ACC-NETWORK-INVALID-NETWORK-NAME", "네트워크 이름이 유효하지 않습니다."),
    INVALID_NETWORK_MTU(400, "ACC-NETWORK-INVALID-NETWORK-MTU", "네트워크 MTU 값이 유효하지 않습니다."),

    INVALID_SUBNET_CIDR( 400, "ACC-NETWORK-INVALID-SUBNET-CIDR", "서브넷 CIDR 값이 유효하지 않습니다."),
    INVALID_SUBNET_NAME(400, "ACC-NETWORK-INVALID-SUBNET-NAME", "서브넷 이름이 유효하지 않습니다."),

    INVALID_SECURITY_GROUP_ID(400, "ACC-NETWORK-INVALID-SECURITY-GROUP-ID", "보안 그룹 ID가 유효하지 않습니다."),
    INVALID_SECURITY_RULE_PROTOCOL(400, "ACC-NETWORK-INVALID-SECURITY-RULE-PROTOCOL", "보안 규칙의 프로토콜이 유효하지 않습니다."),
    INVALID_SECURITY_RULE_DIRECTION(400, "ACC-NETWORK-INVALID-SECURITY-RULE-DIRECTION", "보안 규칙의 방향이 유효하지 않습니다."),
    INVALID_SECURITY_RULE_PORT_RANGE(400, "ACC-NETWORK-INVALID-SECURITY-RULE-PORT-RANGE", "보안 규칙의 포트 범위가 유효하지 않습니다."),
    INVALID_SECURITY_RULE_SECURITY_GROUP_ID_OR_CIDR(400, "ACC-NETWORK-INVALID-SECURITY-RULE-SECURITY-GROUP-ID-OR-CIDR", "보안 규칙의 보안 그룹 ID 또는 CIDR이 유효하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;

    NetworkErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
