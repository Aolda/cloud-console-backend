package com.acc.global.exception.network;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum NeutronErrorCode implements ErrorCode {

    NEUTRON_FLOATING_IP_CREATION_FAILED(500, "ACC-NETWORK-NEUTRON-FLOATING-IP-CREATION-FAILED", "Neutron 플로팅 IP 생성에 실패했습니다."),
    NEUTRON_FLOATING_IP_RELEASE_FAILED(500, "ACC-NETWORK-NEUTRON-FLOATING-IP-RELEASE-FAILED", "Neutron 플로팅 IP 해제에 실패했습니다."),
    NEUTRON_FLOATING_IP_RETRIEVAL_FAILED(500, "ACC-NETWORK-NEUTRON-FLOATING-IP-RETRIEVAL-FAILED", "Neutron 플로팅 IP 조회에 실패했습니다."),

    NEUTRON_PORT_CREATION_FAILED(500, "ACC-NETWORK-NEUTRON-PORT-CREATION-FAILED", "Neutron 포트 생성에 실패했습니다."),
    NEUTRON_PORT_DELETION_FAILED(500, "ACC-NETWORK-NEUTRON-PORT-DELETION-FAILED", "Neutron 포트 삭제에 실패했습니다."),
    NEUTRON_PORT_RETRIEVAL_FAILED(500, "ACC-NETWORK-NEUTRON-PORT-RETRIEVAL-FAILED", "Neutron 포트 조회에 실패했습니다."),

    NEUTRON_ROUTER_CREATION_FAILED(500, "ACC-NETWORK-NEUTRON-ROUTER-CREATION-FAILED", "Neutron 라우터 생성에 실패했습니다."),
    NEUTRON_ROUTER_DELETION_FAILED(500, "ACC-NETWORK-NEUTRON-ROUTER-DELETION-FAILED", "Neutron 라우터 삭제에 실패했습니다."),
    NEUTRON_ROUTER_RETRIEVAL_FAILED(500, "ACC-NETWORK-NEUTRON-ROUTER-RETRIEVAL-FAILED", "Neutron 라우터 조회에 실패했습니다."),

    NEUTRON_SUBNET_CREATION_FAILED(500, "ACC-NETWORK-NEUTRON-SUBNET-CREATION-FAILED", "Neutron 서브넷 생성에 실패했습니다."),
    NEUTRON_NETWORK_CREATION_FAILED(500, "ACC-NETWORK-NEUTRON-NETWORK-CREATION-FAILED", "Neutron 네트워크 생성에 실패했습니다."),
    NEUTRON_NETWORK_DELETION_FAILED(500, "ACC-NETWORK-NEUTRON-NETWORK-DELETION-FAILED", "Neutron 네트워크 삭제에 실패했습니다."),
    NEUTRON_NETWORK_RETRIEVAL_FAILED(500, "ACC-NETWORK-NEUTRON-NETWORK-RETRIEVAL-FAILED", "Neutron 네트워크 조회에 실패했습니다."),
    NEUTRON_SUBNET_RETRIEVAL_FAILED( 500, "ACC-NETWORK-NEUTRON-SUBNET-RETRIEVAL-FAILED", "Neutron 서브넷 조회에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    NeutronErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
