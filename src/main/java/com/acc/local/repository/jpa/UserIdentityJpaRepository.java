package com.acc.local.repository.jpa;

import com.acc.local.entity.UserIdentityEntity;
import com.acc.local.entity.id.UserIdentityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserIdentityJpaRepository extends JpaRepository<UserIdentityEntity, UserIdentityId> {

    List<UserIdentityEntity> findByIdUserId(String userId);

    Optional<UserIdentityEntity> findByIdUserIdAndIdAuthType(String userId, Integer authType);

    List<UserIdentityEntity> findByIdUserIdIn(List<String> userIds);
}