package com.acc.local.dto.keypair;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Keypair 동기화를 위한 DTO
 * OpenStack Nova API 응답을 매핑하여 사용
 */
@Getter
@Builder(toBuilder = true)
@ToString
public class KeypairSyncDto {

    private String name; // 프로젝트 내 유일
    private String fingerprint;  // 전역 유일
    private String userId; // OpenStack keypair 소유자
}

