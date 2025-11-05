package com.acc.global.exception.network;

import com.acc.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum NeutronErrorCode implements ErrorCode {

    NEUTRON_SUBNET_CREATION_FAILED(500, "ACC-NEUTRON-SUBNET-CREATION-FAILED", "Neutron 서브넷 생성에 실패했습니다."),
    NEUTRON_NETWORK_CREATION_FAILED(500, "ACC-NEUTRON-NETWORK-CREATION-FAILED", "Neutron 네트워크 생성에 실패했습니다."),
    NEUTRON_NETWORK_DELETION_FAILED(500, "ACC-NEUTRON-NETWORK-DELETION-FAILED", "Neutron 네트워크 삭제에 실패했습니다."),
    NEUTRON_NETWORK_RETRIEVAL_FAILED(500, "ACC-NEUTRON-NETWORK-RETRIEVAL-FAILED", "Neutron 네트워크 조회에 실패했습니다."),
    NEUTRON_SUBNET_RETRIEVAL_FAILED( 500, "ACC-NEUTRON-SUBNET-RETTRIEVAL-FAILED", "Neutron 서브넷 조회에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    NeutronErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
