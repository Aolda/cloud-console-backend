package com.acc.local.repository.adapters;

import com.acc.local.entity.UserDetailEntity;
import com.acc.local.entity.UserAuthDetailEntity;
import com.acc.local.repository.jpa.UserDetailJpaRepository;
import com.acc.local.repository.jpa.UserAuthDetailJpaRepository;
import com.acc.local.repository.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserDetailJpaRepository userDetailJpaRepository;
    private final UserAuthDetailJpaRepository userAuthDetailJpaRepository;

    @Override
    public UserDetailEntity saveUserDetail(UserDetailEntity userDetailEntity) {
        return userDetailJpaRepository.save(userDetailEntity);
    }

    @Override
    public UserAuthDetailEntity saveUserAuth(UserAuthDetailEntity userAuthDetailEntity) {
        return userAuthDetailJpaRepository.save(userAuthDetailEntity);
    }

    @Override
    public Optional<UserDetailEntity> findUserDetailById(String userId) {
        return userDetailJpaRepository.findById(userId);
    }

    @Override
    public Optional<UserAuthDetailEntity> findUserAuthById(String userId) {
        return userAuthDetailJpaRepository.findById(userId);
    }

    @Override
    public void deleteUserDetailById(String userId) {
        userDetailJpaRepository.deleteById(userId);
    }

    @Override
    public void deleteUserAuthById(String userId) {
        userAuthDetailJpaRepository.deleteById(userId);
    }
}