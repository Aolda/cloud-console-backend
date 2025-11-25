package com.acc.local.dto.project;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import com.acc.global.common.PageRequest;
import com.acc.local.entity.ProjectRequestEntity;

import lombok.Builder;

@Builder
public record ProjectRequestListServiceDto(
	RepositoryPagination pagination,
	List<ProjectRequestDto> projectRequests
) {
	public static ProjectRequestListServiceDto from(List<ProjectRequestDto> savedProjectRequestList, PageRequest pageRequest, boolean isLast, String prevMarker) {

		int currentOffset = (pageRequest == null) ? 0 : Integer.parseInt(new String(
			Base64.getDecoder().decode(pageRequest.getMarker()),
			StandardCharsets.UTF_8
		));
		String nextMarker = Base64.getEncoder().encodeToString(String.valueOf(
			currentOffset + ((pageRequest == null) ? 0 : pageRequest.getLimit())
		).getBytes());

		RepositoryPagination projectRequestPaginationInfo = RepositoryPagination.builder()
			.isFirst(pageRequest == null || pageRequest.getMarker() == null)
			.isLast(isLast)
			.nextMarker(nextMarker)
			.prevMarker(prevMarker)
			.build();

		return ProjectRequestListServiceDto.builder()
			.pagination(projectRequestPaginationInfo)
			.projectRequests(savedProjectRequestList)
			.build();
	}
}
