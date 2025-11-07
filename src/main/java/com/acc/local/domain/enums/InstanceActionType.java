package com.acc.local.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum InstanceActionType {

    ADD_SECURITY_GROUP("보안 그룹 추가"),
    CHANGE_PASSWORD("비밀번호 변경"),
    CONFIRM_RESIZE("크기 변경 확인"),
    CREATE_BACKUP("백업 생성"),
    CREATE_IMAGE("이미지 생성"),
    LOCK("잠금"),
    PAUSE("일시 중지"),
    REBOOT("재부팅"),
    REBUILD("재구축"),
    REMOVE_SECURITY_GROUP("보안 그룹 제거"),
    RESCUE("복구 모드"),
    RESIZE("크기 변경"),
    RESUME("다시 시작"),
    REVERT_RESIZE("크기 변경 롤백"),
    START("시작"),
    STOP("정지"),
    SUSPEND("절전"),
    UNLOCK("잠금 해제"),
    UNPAUSE("일시 중지 해제"),
    UNRESCUE("복구 모드 해제"),
    FORCE_DELETE("강제 삭제"),
    RESTORE("복원"),
    SHELVE("보관"),
    SHELVE_OFFLOAD("보관(오프로드)"),
    UNSHELVE("보관 해제");

    private final String description;

    InstanceActionType(String description) {
        this.description = description;
    }

    public static InstanceActionType findByActionName(String actionName) {
        if (actionName == null || actionName.isEmpty()) {
            return null;
        }
        String upperActionName = actionName.toUpperCase();

        return Arrays.stream(values())
                .filter(action -> action.name().equals(upperActionName))
                .findFirst()
                .orElse(null);
    }
}
