package com.acc.local.repository.adapters;

import com.acc.local.entity.UserEntity;
import com.acc.local.repository.jpa.UserJpaRepository;
import com.acc.local.repository.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserEntity save(UserEntity userEntity) {
        return userJpaRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> findByUserId(String userId) {
        return userJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userJpaRepository.findById(id);
    }
}