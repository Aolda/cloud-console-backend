package com.acc.local.repository.ports;

import com.acc.local.entity.UserTokenEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserTokenRepositoryPort {
    
    UserTokenEntity save(UserTokenEntity userToken);
    
    Optional<UserTokenEntity> findByJwtTokenAndIsActiveTrue(String jwtToken);
    
    Optional<UserTokenEntity> findByUserIdAndIsActiveTrue(Long userId);
    
    void deactivateAllByUserId(Long userId);
    
    void deactivateExpiredTokens();
    
    void deleteInactiveTokens();
}