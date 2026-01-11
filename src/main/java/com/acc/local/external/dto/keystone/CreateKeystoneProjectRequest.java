package com.acc.local.external.dto.keystone;

import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

@Builder
public record CreateKeystoneProjectRequest(
	String projectName,
	String projectDescription
) {
	public Map<String, Object> toKeystoneRequest() {
		Map<String, Object> projectObject = new HashMap<>();
		projectObject.put("name", projectName());

		if (projectDescription() != null) {
			projectObject.put("description", projectDescription());
		}

		Map<String, Object> request = new HashMap<>();
		request.put("project", projectObject);

		return request;
	}
}
