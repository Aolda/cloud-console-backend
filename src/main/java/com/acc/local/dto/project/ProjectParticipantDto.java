package com.acc.local.dto.project;

import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.domain.model.auth.User;
import com.acc.local.entity.ProjectParticipantEntity;
import com.acc.local.entity.UserDetailEntity;

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
		return ProjectParticipantDto.builder()
			.userId(dbProjectParticipant.getUserDetail().getUserId())
			.userName(dbProjectParticipant.getUserDetail().getUserName())
			// .userEmail(dbProjectParticipant.getUserDetail().getUserEmail()) // TODO: User 도메인과 협의필요
			.userPhoneNumber(dbProjectParticipant.getUserDetail().getUserPhoneNumber())
			.role(dbProjectParticipant.getRole())
			.build();
	}
}
