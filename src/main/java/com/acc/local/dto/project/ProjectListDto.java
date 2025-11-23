package com.acc.local.dto.project;

import java.util.*;

import com.acc.local.external.dto.OpenstackPagination;
import com.acc.local.external.dto.keystone.KeystoneProject;

import lombok.Builder;

@Builder
public record ProjectListDto(
	OpenstackPagination pageInfo,
	List<KeystoneProject> projectList
) {
	public static ProjectListDto from(List<KeystoneProject> projectServiceDto, OpenstackPagination pageInfo) {
		return ProjectListDto.builder()
			.pageInfo(pageInfo)
			.projectList(projectServiceDto)
			.build();
	}
}
