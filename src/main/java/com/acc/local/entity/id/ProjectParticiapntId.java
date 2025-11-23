package com.acc.local.entity.id;

import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProjectParticiapntId {

	private String projectId;

	private String userId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProjectParticiapntId ppId = (ProjectParticiapntId) o;
		return Objects.equals(projectId, ppId.projectId) &&
			Objects.equals(userId, ppId.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(projectId, userId);
	}

}
