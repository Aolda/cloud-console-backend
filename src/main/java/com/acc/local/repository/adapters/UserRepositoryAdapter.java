package com.acc.local.repository.adapters;

import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.entity.UserIdentityEntity;
import com.acc.local.repository.dto.UserDBDto;
import com.acc.local.repository.jpa.UserDetailJpaRepository;
import com.acc.local.repository.jpa.UserIdentityJpaRepository;
import com.acc.local.repository.modules.UserQueryDSLModule;
import com.acc.local.repository.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserDetailJpaRepository userDetailJpaRepository;
    private final UserIdentityJpaRepository userIdentityJpaRepository;
    private final UserQueryDSLModule userQueryDSLModule;

    @Override
    public UserDbExtraEntity saveUserDetail(UserDbExtraEntity userDbExtraEntity) {
        return userDetailJpaRepository.save(userDbExtraEntity);
    }

    @Override
    public UserIdentityEntity saveUserIdentity(UserIdentityEntity userIdentityEntity) {
        return userIdentityJpaRepository.save(userIdentityEntity);
    }

    @Override
    public Optional<UserDbExtraEntity> findUserDetailById(String userId) {
        return userDetailJpaRepository.findById(userId);
    }

    // UserId로만 단일 조회시, 가장 최신의 UserIdentityEntity를 조회하도록 설정.
    @Override
    public Optional<UserIdentityEntity> findUserAuthById(String userId) {
        return userIdentityJpaRepository.findByIdUserId(userId).stream()
                .max(Comparator.comparing(UserIdentityEntity::getCreatedAt));
    }

    @Override
    public List<UserIdentityEntity> findUserAuthsByUserId(String userId) {
        return userIdentityJpaRepository.findByIdUserId(userId);
    }

    @Override
    public Optional<UserIdentityEntity> findUserAuthByIdAndAuthType(String userId, Integer authType) {
        return userIdentityJpaRepository.findByIdUserIdAndIdAuthType(userId, authType);
    }

    @Override
    public List<UserDbExtraEntity> findUserDetailsByIds(List<String> userIds) {
        return userDetailJpaRepository.findAllById(userIds);
    }

    @Override
    public List<UserIdentityEntity> findUserAuthsByIds(List<String> userIds) {
        return userIdentityJpaRepository.findByIdUserIdIn(userIds);
    }

    @Override
    public List<UserDbExtraEntity> findUserByUserName(String userName) {
        return userDetailJpaRepository.findAllByUserName(userName);
    }

    @Override
    public Optional<UserDBDto> findUserDBByUserId(String userId) {
        return userQueryDSLModule.findUserByUserId(userId);
    }

    @Override
    public List<UserDBDto> findUserDBsByUserIds(List<String> userIds) {
        return userQueryDSLModule.findUsersByUserIds(userIds);
    }
}
