package com.acc.local.repository.adapters;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.acc.local.entity.UnivDepartInfoEntity;
import com.acc.local.repository.jpa.UnivDepartmentJpaRepository;
import com.acc.local.repository.ports.UnivDepartmentRepositoryPort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UnivDepartmentRepositoryAdapter implements UnivDepartmentRepositoryPort {

	private final UnivDepartmentJpaRepository univDepartmentJpaRepository;

	@Override
	public void save(UnivDepartInfoEntity departInfo) {
		this.univDepartmentJpaRepository.save(departInfo);
	}

	@Override
	public List<UnivDepartInfoEntity> findByFirstMajor(String firstMajor) {
		return univDepartmentJpaRepository.findAllByFirstMajor(firstMajor);
	}
}
