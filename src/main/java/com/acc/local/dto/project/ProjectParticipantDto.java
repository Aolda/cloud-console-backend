package com.acc.local.dto.project;

import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.entity.ProjectParticipantEntity;

import lombok.Builder;

@Builder
public record ProjectParticipantDto(
	String userId,
	String userName,
	String userEmail,
	String userPhoneNumber,
	ProjectRole role
) {
	public static ProjectParticipantDto from(ProjectParticipantEntity dbProjectParticipant) {
		return from(dbProjectParticipant, null);
	}

	public static ProjectParticipantDto from(ProjectParticipantEntity dbProjectParticipant, String userEmail) {
		return ProjectParticipantDto.builder()
			.userId(dbProjectParticipant.getUserDetail().getUserId())
			.userName(dbProjectParticipant.getUserDetail().getUserName())
			.userEmail(userEmail)
			.userPhoneNumber(dbProjectParticipant.getUserDetail().getUserPhoneNumber())
			.role(dbProjectParticipant.getRole())
			.build();
	}
}
