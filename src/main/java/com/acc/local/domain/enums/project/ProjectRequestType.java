package com.acc.local.domain.enums.project;

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
	public String toString() {
		return this.getRequestTypeId();
	}

}
