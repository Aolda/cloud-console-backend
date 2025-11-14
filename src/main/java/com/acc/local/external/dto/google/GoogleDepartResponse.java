package com.acc.local.external.dto.google;

import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Organization;
import com.google.api.services.people.v1.model.Person;

public record GoogleDepartResponse(
	String firstName,
	String lastName,
	String email,
	String major,
	String gradeStage,
	String status
) {
	public static GoogleDepartResponse create(Person googlePersonProfile) {
		EmailAddress userGoogleEmail = googlePersonProfile.getEmailAddresses().getFirst();
		Name userGoogleName = googlePersonProfile.getNames().getFirst();
		Organization userGoogleOrg = googlePersonProfile.getOrganizations().getFirst();

		return new GoogleDepartResponse(
			userGoogleName.getGivenName(),
			userGoogleName.getFamilyName(),
			userGoogleEmail.getValue(),
			userGoogleOrg.getDepartment(),
			userGoogleOrg.getTitle(),
			userGoogleOrg.getJobDescription()
		);
	}
}
