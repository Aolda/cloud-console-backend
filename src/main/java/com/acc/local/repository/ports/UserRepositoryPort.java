package com.acc.local.repository.ports;

import com.acc.local.entity.UserDetailEntity;
import com.acc.local.entity.UserAuthDetailEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    UserDetailEntity saveUserDetail(UserDetailEntity userDetailEntity);

    UserAuthDetailEntity saveUserAuth(UserAuthDetailEntity userAuthDetailEntity);

    Optional<UserDetailEntity> findUserDetailById(String userId);

    Optional<UserAuthDetailEntity> findUserAuthById(String userId);

    List<UserDetailEntity> findUserDetailsByIds(List<String> userIds);

    List<UserAuthDetailEntity> findUserAuthsByIds(List<String> userIds);

    void deleteUserDetailById(String userId);

    void deleteUserAuthById(String userId);
}
