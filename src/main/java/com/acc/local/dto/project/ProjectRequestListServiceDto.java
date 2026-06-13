package com.acc.local.dto.project;

import java.util.List;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PaginationUtils;

import lombok.Builder;

@Builder
public record ProjectRequestListServiceDto(
	RepositoryPagination pagination,
	List<ProjectRequestDto> projectRequests
) {
	public static ProjectRequestListServiceDto from(
		List<ProjectRequestDto> savedProjectRequestList,
		PageRequest pageRequest,
		int currentOffset,
		boolean hasNext
	) {
		String nextMarker = hasNext
			? PaginationUtils.encodeOffsetMarker(currentOffset + savedProjectRequestList.size())
			: null;
		String prevMarker = currentOffset > 0
			? PaginationUtils.encodeOffsetMarker(currentOffset)
			: null;

		RepositoryPagination projectRequestPaginationInfo = RepositoryPagination.builder()
			.isFirst(currentOffset == 0)
			.isLast(!hasNext)
			.nextMarker(nextMarker)
			.prevMarker(prevMarker)
			.build();

		return ProjectRequestListServiceDto.builder()
			.pagination(projectRequestPaginationInfo)
			.projectRequests(savedProjectRequestList)
			.build();
	}
}
