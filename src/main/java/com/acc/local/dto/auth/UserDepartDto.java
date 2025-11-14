package com.acc.local.dto.auth;

import com.acc.local.domain.enums.UnivAccountType;
import com.acc.local.domain.enums.UnivDepartStatus;
import com.acc.local.entity.UnivDepartInfoEntity;
import com.acc.local.external.dto.google.GoogleDepartResponse;
import lombok.Getter;

public record UserDepartDto(
	String firstMajor,
	String college,
	String department,
	int grade,
	UnivAccountType univAccountType,
	UnivDepartStatus univDepartStatus
) {
	public static UserDepartDto create(GoogleDepartResponse userDepartInfo, UnivDepartInfoEntity predictedUserDepartment) {
		return new UserDepartDto(
			userDepartInfo.major(),
			predictedUserDepartment.getCollege(),
			predictedUserDepartment.getDepartment(),
			predictUserGrade(userDepartInfo),
			UnivAccountType.getType(userDepartInfo.status()),
			UnivDepartStatus.getUnivDepartStatus(userDepartInfo.status())
		);
	}

	private static int predictUserGrade(GoogleDepartResponse userDepartInfo) {
		UnivAccountType accountType = UnivAccountType.getType(userDepartInfo.status());
		if (accountType != UnivAccountType.UNDERGRADUATE) return -1;
		return Integer.parseInt(userDepartInfo.gradeStage().substring(0, 1));
	}
}
