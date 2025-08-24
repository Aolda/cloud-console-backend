package com.acc.local.repository.ports;

import com.acc.local.entity.UserTokenEntity;

import java.util.Optional;

public interface UserTokenRepositoryPort {
    
    UserTokenEntity save(UserTokenEntity userToken);
    
    Optional<UserTokenEntity> findByJwtTokenAndIsActiveTrue(String jwtToken);
    
    Optional<UserTokenEntity> findByUserIdAndIsActiveTrue(String userId);
    
    void deactivateAllByUserId(String userId);
    
    void deactivateExpiredTokens();
    
    void deleteInactiveTokens();
}