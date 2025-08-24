package com.acc.local.repository.ports;

import com.acc.local.entity.UserEntity;

import java.util.Optional;

public interface UserRepositoryPort {
    UserEntity save(UserEntity userEntity);
    Optional<UserEntity> findByUserId(String userId);
    Optional<UserEntity> findById(Long id);
}