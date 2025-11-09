package com.acc.local.repository.jpa;

import com.acc.local.entity.UserAuthDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthDetailJpaRepository extends JpaRepository<UserAuthDetailEntity, String> {
}