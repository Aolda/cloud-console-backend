package com.acc.local.dto.auth;

import com.acc.local.domain.enums.UnivAccountType;

import lombok.Builder;

@Builder
public record UnivDepartBriefDto(
	String grade,
	String univDepartment
) {
	public static UnivDepartBriefDto from(UserDepartDto userDepartDto) {
		if (userDepartDto.univAccountType() != UnivAccountType.UNDERGRADUATE) {
			return null;
		}

		return UnivDepartBriefDto.builder()
			.grade(userDepartDto.grade() + "학년")
			.univDepartment(userDepartDto.department())
			.build();
	}

	public static UnivDepartBriefDto from(AdminGetUserResponse adminGetUserResponse) {
		return UnivDepartBriefDto.builder()
			// .grade(adminGetUserResponse.)
			.grade(1 + "학년") // TODO: 회원가입 시 나이 저장되도록 수정 필요
			.univDepartment(adminGetUserResponse.department())
			.build();
	}
}
