package com.acc.local.domain.enums.project;

import com.acc.global.exception.project.ProjectErrorCode;
import com.acc.global.exception.project.ProjectServiceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectRequestType {
	CAPSTONE_DESIGN("PROJECT_REQUEST_TYPE/CAPSTONE_DESIGN", "캡스톤디자인"),
	MAJOR_LECTURE("PROJECT_REQUEST_TYPE/MAJOR_LECTURE", "전공강의"),
	CLUB_ACTIVITY("PROJECT_REQUEST_TYPE/CLUB_ACTIVITY", "동아리 활동"),
	PERSONAL_PROJECT("PROJECT_REQUEST_TYPE/PERSONAL_PROJECT", "개인 프로젝트"),
	ETC("PROJECT_REQUEST_TYPE/ETC", "기타")
	;

	private final String requestTypeId;
	private final String requestTypeName;

	@Override
	@JsonValue
	public String toString() {
		return this.getRequestTypeId();
	}

	@JsonCreator
	public static ProjectRequestType fromTypeId(String requestTypeId) {
		for (ProjectRequestType type : ProjectRequestType.values()) {
			if (type.requestTypeId.equalsIgnoreCase(requestTypeId)) {
				return type;
			}
		}

		throw new ProjectServiceException(ProjectErrorCode.INVALID_PROJECT_REQUEST_TYPE);
	}

}
