package com.acc.local.repository.ports;

import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.UserIdentityEntity;
import com.acc.local.repository.dto.UserDBDto;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    UserDbExtraEntity saveUserDetail(UserDbExtraEntity userDbExtraEntity);

    UserIdentityEntity saveUserIdentity(UserIdentityEntity userIdentityEntity);

    Optional<UserDbExtraEntity> findUserDetailById(String userId);
    // 가장 최근 인증한 정보 조회
    Optional<UserIdentityEntity> findUserAuthById(String userId);
    // User가 인증한 모든 정보 조회
    List<UserIdentityEntity> findUserAuthsByUserId(String userId);
    // 단일 인증정보 조회
    Optional<UserIdentityEntity> findUserAuthByIdAndAuthType(String userId, Integer authType);

    List<UserDbExtraEntity> findUserDetailsByIds(List<String> userIds);

    List<UserIdentityEntity> findUserAuthsByIds(List<String> userIds);

	List<UserDbExtraEntity> findUserByUserName(String userName);

    /**
     * userId로 User 관련 정보를 조인하여 조회
     * UserIdentity와 UserDbExtra를 inner join으로 조회
     * 이때, UserIdentity 정보는 가장 최근 인증된 정보이다.
     */
    Optional<UserDBDto> findUserDBByUserId(String userId);

    /**
     * 여러 userId로 User 관련 정보를 조인하여 bulk 조회
     * UserIdentity와 UserDbExtra를 inner join으로 조회
     * 삭제되지 않은 사용자만 반환
     * 이때, UserIdentity 정보는 가장 최근 인증된 정보이다.
     */
    List<UserDBDto> findUserDBsByUserIds(List<String> userIds);

    Optional<UserDbExtraEntity> findUserDetailByKeycloakUserId(String keycloakUserId);

    // Account Linking: 이메일로 기존 UserIdentity 조회 (keycloakUserId 미연결 사용자 탐색)
    Optional<UserIdentityEntity> findUserIdentityByEmail(String email);
}
