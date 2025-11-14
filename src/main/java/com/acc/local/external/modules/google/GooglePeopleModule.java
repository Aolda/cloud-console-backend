package com.acc.local.external.modules.google;

import java.io.IOException;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.acc.global.exception.google.GoogleErrorCode;
import com.acc.global.exception.google.GoogleException;
import com.acc.local.external.dto.google.GoogleDepartResponse;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GooglePeopleModule {

	private final GoogleCredentialsModule googleCredentialsModule;

	public GoogleDepartResponse getUserDepartInfo(OAuth2User oAuth2User) {
		PeopleService peopleService = googleCredentialsModule.getPeopleService(oAuth2User);
		Person userProfile = requestUserGoogleProfile(peopleService);

		return GoogleDepartResponse.create(userProfile);
	}

	private static Person requestUserGoogleProfile(PeopleService peopleService) {
		try {
			String resourceName = "people/me";
			String personFields = "names,organizations,emailAddresses";

			return peopleService.people()
				.get(resourceName)
				.setPersonFields(personFields)
				.execute();
		} catch (IOException e) {
			throw new GoogleException(GoogleErrorCode.API_READ_FAILURE, e);
		}
	}

}
