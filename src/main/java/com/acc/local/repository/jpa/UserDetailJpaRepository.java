package com.acc.local.repository.jpa;

import java.util.List;

import com.acc.local.entity.UserDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailJpaRepository extends JpaRepository<UserDetailEntity, String> {

	List<UserDetailEntity> findAllByUserName(String username);
}
