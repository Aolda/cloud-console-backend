package com.acc.local.repository.jpa;

import java.util.List;
import java.util.Optional;

import com.acc.local.entity.UserDbExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailJpaRepository extends JpaRepository<UserDbExtraEntity, String> {

	List<UserDbExtraEntity> findAllByUserName(String username);

	Optional<UserDbExtraEntity> findByKeycloakUserId(String keycloakUserId);
}
