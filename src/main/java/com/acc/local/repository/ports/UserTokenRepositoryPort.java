package com.acc.local.repository.ports;

import com.acc.local.entity.UserTokenEntity;

import java.util.List;
import java.util.Optional;

public interface UserTokenRepositoryPort {
    
    UserTokenEntity save(UserTokenEntity userToken);
    
    Optional<UserTokenEntity> findByJwtTokenAndIsActiveTrue(String jwtToken);

    List<UserTokenEntity> findAllByUserIdAndIsActiveTrue(String userId);

    void deactivateAllByUserId(String userId);
    
    void deactivateExpiredTokens();

    void deleteInactiveTokens();

    Optional<UserTokenEntity> findLatestByUserId(String userId);
}
