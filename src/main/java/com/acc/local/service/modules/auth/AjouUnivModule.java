package com.acc.local.service.modules.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.acc.local.domain.enums.UnivAccountType;
import com.acc.local.domain.enums.UnivDepartStatus;
import com.acc.local.dto.auth.KeycloakIdTokenClaims;
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

	public Optional<UserDepartDto> getUserDepartInfoFromKeycloakClaims(KeycloakIdTokenClaims claims) {
		if (claims.ajouMajor() == null || claims.ajouMajor().isBlank()) return Optional.empty();
		if (claims.ajouStatus() == null || claims.ajouStatus().isBlank()) return Optional.empty();

		String statusCode = extractStatusCode(claims.ajouStatus());
		UnivDepartInfoEntity dept = predictUserDepartProfile(claims.ajouMajor());
		int grade = resolveGrade(statusCode, claims.ajouGrade());

		return Optional.of(new UserDepartDto(
				claims.ajouMajor(),
				dept != null ? dept.getCollege() : null,
				dept != null ? dept.getDepartment() : claims.ajouMajor(),
				grade,
				UnivAccountType.getType(statusCode),
				UnivDepartStatus.getUnivDepartStatus(statusCode)
		));
	}

	private String extractStatusCode(String rawStatus) {
		int idx = rawStatus.indexOf('(');
		return idx > 0 ? rawStatus.substring(0, idx).trim() : rawStatus.trim();
	}

	private int resolveGrade(String statusCode, String ajouGrade) {
		if (UnivAccountType.getType(statusCode) != UnivAccountType.UNDERGRADUATE) return -1;
		try { return Integer.parseInt(ajouGrade.trim()); }
		catch (NumberFormatException e) { return -1; }
	}

	private UnivDepartInfoEntity predictUserDepartProfile(GoogleDepartResponse userDepartInfo) {
		return predictUserDepartProfile(userDepartInfo.major());
	}

	private UnivDepartInfoEntity predictUserDepartProfile(String firstMajor) {
		List<UnivDepartInfoEntity> candidates = univDepartmentRepository.findByFirstMajor(firstMajor);
		return candidates.isEmpty() ? null : candidates.getFirst();
	}

}
