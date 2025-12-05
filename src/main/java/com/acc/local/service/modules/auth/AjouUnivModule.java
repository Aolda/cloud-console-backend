package com.acc.local.service.modules.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.acc.local.dto.auth.UserDepartDto;
import com.acc.local.entity.UnivDepartInfoEntity;
import com.acc.local.external.dto.google.GoogleDepartResponse;
import com.acc.local.external.modules.google.GooglePeopleModule;
import com.acc.local.repository.ports.UnivDepartmentRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AjouUnivModule {

	private final UnivDepartmentRepositoryPort univDepartmentRepository;
	private final GooglePeopleModule googlePeopleModule;

	public Optional<UserDepartDto> getUserDepartInfo(OAuth2User googleOAuth2UserObject) {
		GoogleDepartResponse userDepartInfo = googlePeopleModule.getUserDepartInfo(googleOAuth2UserObject);
		if (userDepartInfo == null) {
			return Optional.empty();
		}
		UnivDepartInfoEntity predictedUserDepartment = predictUserDepartProfile(userDepartInfo);

		return Optional.of(UserDepartDto.create(userDepartInfo, predictedUserDepartment));
	}

	private UnivDepartInfoEntity predictUserDepartProfile(GoogleDepartResponse userDepartInfo) {
		List<UnivDepartInfoEntity> departmentInfoCandidates = univDepartmentRepository.findByFirstMajor(userDepartInfo.major());

		if (departmentInfoCandidates.isEmpty()) {
			return null;
		}
		return departmentInfoCandidates.getFirst();
	}

}
