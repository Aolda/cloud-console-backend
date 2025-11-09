package com.acc.local.repository.ports;

import java.util.List;

import com.acc.local.entity.UnivDepartInfoEntity;

public interface UnivDepartmentRepositoryPort {

	void save(UnivDepartInfoEntity departInfo);

	List<UnivDepartInfoEntity> findByFirstMajor(String firstMajor);

}
