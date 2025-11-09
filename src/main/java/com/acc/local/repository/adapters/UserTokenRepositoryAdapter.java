package com.acc.local.repository.adapters;

import com.acc.local.entity.UserTokenEntity;
import com.acc.local.repository.jpa.UserTokenJpaRepository;
import com.acc.local.repository.ports.UserTokenRepositoryPort;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
public class UserTokenRepositoryAdapter implements UserTokenRepositoryPort {
    
    private final UserTokenJpaRepository userTokenJpaRepository;

    @Override
    public UserTokenEntity save(UserTokenEntity userToken) {
        return userTokenJpaRepository.save(userToken);
    }

    @Override
    public Optional<UserTokenEntity> findByJwtTokenAndIsActiveTrue(String jwtToken) {
        return userTokenJpaRepository.findByJwtTokenAndIsActiveTrue(jwtToken);
    }

    @Override
    public List<UserTokenEntity> findAllByUserIdAndIsActiveTrue(String userId) {
        return userTokenJpaRepository.findAllByUserIdAndIsActiveTrue(userId);
    }

    @Override
    @Transactional
    public void deactivateAllByUserId(String userId) {
        userTokenJpaRepository.deactivateAllByUserId(userId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deactivateExpiredTokens() {
        userTokenJpaRepository.deactivateExpiredTokens(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteInactiveTokens() {
        userTokenJpaRepository.deleteByIsActiveFalse();
    }
}
