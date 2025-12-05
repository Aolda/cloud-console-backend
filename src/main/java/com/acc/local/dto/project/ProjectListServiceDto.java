package com.acc.local.dto.project;

import java.util.List;

import com.acc.local.external.dto.OpenstackPagination;

import lombok.Builder;

@Builder
public record ProjectListServiceDto(
	OpenstackPagination pagination,
	List<ProjectServiceDto> projects
) {}
