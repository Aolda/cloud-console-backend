package com.acc.local.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum InstanceStatus {

    // --- 1. 정상 작동 및 정지 상태 ---
    ACTIVE("활성"),
    SHUTOFF("정지"),
    PAUSED("일시 중지"),
    SUSPENDED("절전"),
    SHELVED("보관됨"),
    SHELVED_OFFLOADED("보관(오프로드)"),

    // --- 2. 변경 및 진행 중 상태 ---
    BUILD("생성 중"),
    REBOOT("재부팅 중"),
    HARD_REBOOT("하드 재부팅 중"),
    REBUILD("재구축 중"),
    RESIZE("크기 변경 중"),
    VERIFY_RESIZE("크기 변경 확인"),
    PASSWORD("비밀번호 변경 중"),
    RESCUE("복구 모드"),

    // --- 3. 삭제 상태 ---
    DELETED("삭제됨"),

    // --- 4. 오류 및 알 수 없는 상태 ---
    ERROR("오류"),
    REVERT_RESIZE("크기 변경 롤백"),
    UNKNOWN("알 수 없음");

    private final String description;

    InstanceStatus(String description) {
        this.description = description;
    }

    public static InstanceStatus findByStatusName(String statusName) {
        if (statusName == null || statusName.isEmpty()) {
            return UNKNOWN;
        }

        String upperStatusName = statusName.toUpperCase();

        return Arrays.stream(values())
                .filter(status -> status.name().equals(upperStatusName))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
