package com.acc.local.dto.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.acc.global.common.PageRequest;

import lombok.Builder;

@Builder
public record ProjectRequestListServiceDto(
	RepositoryPagination pagination,
	List<ProjectRequestDto> projectRequests
) {
	public static ProjectRequestListServiceDto from(
		List<ProjectRequestDto> fetchedProjectRequestList,
		PageRequest pageRequest
	) {
		int limit = pageRequest.getLimit();
		boolean isPrevious = pageRequest.getDirection() == PageRequest.Direction.prev && pageRequest.getMarker() != null;
		boolean hasMore = fetchedProjectRequestList.size() > limit;
		List<ProjectRequestDto> projectRequestList = new ArrayList<>(
			hasMore ? fetchedProjectRequestList.subList(0, limit) : fetchedProjectRequestList
		);

		if (isPrevious) {
			Collections.reverse(projectRequestList);
		}

		if (projectRequestList.isEmpty()) {
			RepositoryPagination emptyPagination = RepositoryPagination.builder()
				.isFirst(pageRequest.getMarker() == null || isPrevious)
				.isLast(!isPrevious)
				.nextMarker(null)
				.prevMarker(null)
				.build();

			return ProjectRequestListServiceDto.builder()
				.pagination(emptyPagination)
				.projectRequests(projectRequestList)
				.build();
		}

		String firstProjectRequestId = projectRequestList.getFirst().projectRequestId();
		String lastProjectRequestId = projectRequestList.getLast().projectRequestId();
		boolean isFirst = isPrevious ? !hasMore : pageRequest.getMarker() == null;
		boolean isLast = isPrevious ? false : !hasMore;
		String nextMarker = isLast ? null : lastProjectRequestId;
		String prevMarker = isFirst ? null : firstProjectRequestId;

		RepositoryPagination projectRequestPaginationInfo = RepositoryPagination.builder()
			.isFirst(isFirst)
			.isLast(isLast)
			.nextMarker(nextMarker)
			.prevMarker(prevMarker)
			.build();

		return ProjectRequestListServiceDto.builder()
			.pagination(projectRequestPaginationInfo)
			.projectRequests(projectRequestList)
			.build();
	}
}
