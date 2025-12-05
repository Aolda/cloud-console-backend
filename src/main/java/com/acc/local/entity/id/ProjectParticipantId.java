package com.acc.local.entity.id;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProjectParticipantId implements Serializable {

	private String projectId;

	private String userId;

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProjectParticipantId ppId = (ProjectParticipantId) o;
		return Objects.equals(projectId, ppId.projectId) &&
			Objects.equals(userId, ppId.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(projectId, userId);
	}

}
