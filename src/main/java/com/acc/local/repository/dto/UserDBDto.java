package com.acc.local.repository.dto;

import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.UserIdentityEntity;

/**
 * User 관련 DB 정보를 한 번에 조회하기 위한 DTO
 * UserIdentity와 UserDbExtra를 조인해서 가져옴 (QueryDSL Projection용)
 */
public record UserDBDto(
        UserIdentityEntity userIdentity,
        UserDbExtraEntity userDbExtra
) {
    /**
     * userId 반환 (편의 메서드)
     */
    public String getUserId() {
        return userDbExtra.getUserId();
    }
}