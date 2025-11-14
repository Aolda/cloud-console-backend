package com.acc.local.service.modules.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.acc.local.domain.enums.UnivAccountType;
import com.acc.local.domain.enums.UnivDepartStatus;
import com.acc.local.dto.auth.UserDepartDto;
import com.acc.local.entity.UnivDepartInfoEntity;
import com.acc.local.external.dto.google.GoogleDepartResponse;
import com.acc.local.external.modules.google.GooglePeopleModule;
import com.acc.local.repository.ports.UnivDepartmentRepositoryPort;

@ExtendWith(MockitoExtension.class)
class AjouUnivModuleTest {

	@Mock
	private UnivDepartmentRepositoryPort univDepartmentRepository;
	@Mock
	private GooglePeopleModule googlePeopleModule;

	@InjectMocks
	private AjouUnivModule ajouUnivModule;

	@BeforeEach
	void setUp() {

	}

	@Test
	@DisplayName("이용자는 본인의 아주대학교 계정 로그인정보를 통해 재학상태를 인증할 수 있다")
	void givenOAuth2User_whenUserAuthorizeUnivAccount_thenReturnUnivDepartInfo() {
		// given
		OAuth2User oAuth2User = mock(OAuth2User.class);

		String testFirstMajor = "testFirstMajor";
		String testCollege = "testCollege";
		String testDeartment = "testDeartment";

		UnivDepartInfoEntity mockedEntity = UnivDepartInfoEntity.builder()
			.firstMajor(testFirstMajor)
			.college(testCollege)
			.department(testDeartment)
			.build();
		GoogleDepartResponse mockedGoogleModuleReturn = new GoogleDepartResponse(
			"testFirstName",
			"testLastName",
			"test@test.ac.kr",
			testFirstMajor,
			"4학년",
			"SS0001"
		);

		when(googlePeopleModule.getUserDepartInfo(any())).thenReturn(mockedGoogleModuleReturn);
		when(univDepartmentRepository.findByFirstMajor(any())).thenReturn(List.of(mockedEntity));

		Optional<UserDepartDto> expectUserDepartDto = Optional.of(new UserDepartDto(
			testFirstMajor,
			testCollege,
			testDeartment,
			4,
			UnivAccountType.UNDERGRADUATE,
			UnivDepartStatus.ATTENDING
		));

		// when
		Optional<UserDepartDto> actualUserDepartInfo = ajouUnivModule.getUserDepartInfo(oAuth2User);

		// then
		assertEquals(expectUserDepartDto, actualUserDepartInfo);
	}
}
