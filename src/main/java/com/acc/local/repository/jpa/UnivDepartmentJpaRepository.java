package com.acc.local.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acc.local.entity.UnivDepartInfoEntity;

@Repository
public interface UnivDepartmentJpaRepository extends JpaRepository<UnivDepartInfoEntity, Long> {

	List<UnivDepartInfoEntity> findAllByFirstMajor(String firstMajor);

	UnivDepartInfoEntity save(UnivDepartInfoEntity univDepartInfoEntity);

}
