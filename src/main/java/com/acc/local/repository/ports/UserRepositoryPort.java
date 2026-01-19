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

    Optional<UserIdentityEntity> findUserAuthById(String userId);

    List<UserDbExtraEntity> findUserDetailsByIds(List<String> userIds);

    List<UserIdentityEntity> findUserAuthsByIds(List<String> userIds);

    void deleteUserDetailById(String userId);

    void deleteUserAuthById(String userId);

	List<UserDbExtraEntity> findUserByUserName(String userName);

    /**
     * userId로 User 관련 정보를 조인하여 조회
     * UserIdentity와 UserDbExtra를 inner join으로 조회
     */
    Optional<UserDBDto> findUserDBByUserId(String userId);

    /**
     * 여러 userId로 User 관련 정보를 조인하여 bulk 조회
     * UserIdentity와 UserDbExtra를 inner join으로 조회
     * 삭제되지 않은 사용자만 반환
     */
    List<UserDBDto> findUserDBsByUserIds(List<String> userIds);
}
