package com.acc.local.repository.ports;

import com.acc.local.entity.UserDetailEntity;
import com.acc.local.entity.UserAuthDetailEntity;

import java.util.Optional;

public interface UserRepositoryPort {

    UserDetailEntity saveUserDetail(UserDetailEntity userDetailEntity);

    UserAuthDetailEntity saveUserAuth(UserAuthDetailEntity userAuthDetailEntity);

    Optional<UserDetailEntity> findUserDetailById(String userId);

    Optional<UserAuthDetailEntity> findUserAuthById(String userId);

    void deleteUserDetailById(String userId);

    void deleteUserAuthById(String userId);
}
