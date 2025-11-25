package com.acc.local.entity;

import java.time.LocalDateTime;

import com.acc.local.domain.enums.project.ProjectRole;
import com.acc.local.entity.id.ProjectParticipantId;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class ProjectParticipantEntity {

	@EmbeddedId
	private ProjectParticipantId projectParticipantId;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("projectId")
	@JoinColumn(name = "project_id")
	private ProjectEntity project;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private UserDetailEntity userDetail;

	@JoinColumn(name = "role")
	private ProjectRole role;

	@JoinColumn(name = "created_at")
	private LocalDateTime createdAt;
}
