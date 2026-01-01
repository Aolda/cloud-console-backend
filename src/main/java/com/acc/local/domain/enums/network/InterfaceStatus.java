package com.acc.local.domain.enums.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum InterfaceStatus {
    ACTIVE("활성화"),
    DOWN("비활성화"),
    BUILD("생성 중"),
    ERROR("오류"),
    UNKNOWN("알 수 없음");

    private final String description;

    public static final Map<String, InterfaceStatus> STATUS_MAP = Map.of(
            "ACTIVE", ACTIVE,
            "DOWN", DOWN,
            "BUILD", BUILD,
            "ERROR", ERROR,
            "UNKNOWN", UNKNOWN
    );


    @JsonCreator
    public static InterfaceStatus findByStatusName(String statusName) {
        if (statusName == null || statusName.isEmpty() || !STATUS_MAP.containsKey(statusName.toUpperCase())) {
            return UNKNOWN;
        }
        String upperStatusName = statusName.toUpperCase();

        return STATUS_MAP.get(upperStatusName);
    }
}
