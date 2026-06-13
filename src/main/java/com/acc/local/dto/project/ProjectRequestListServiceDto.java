package com.acc.local.dto.project;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PaginationUtils;

import java.util.List;

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
		PageRequest normalized = PaginationUtils.normalize(pageRequest, false);
		int previousOffset = Math.max(currentOffset - normalized.getLimit(), 0);
		String nextMarker = hasNext
			? PaginationUtils.encodeOffsetMarker(currentOffset + savedProjectRequestList.size())
			: null;
		String prevMarker = currentOffset > 0
			? PaginationUtils.encodeOffsetMarker(previousOffset)
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
