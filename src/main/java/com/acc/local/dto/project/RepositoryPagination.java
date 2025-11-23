package com.acc.local.dto.project;

import lombok.Builder;

@Builder
public record RepositoryPagination(
	boolean isFirst,
	boolean isLast,
	String nextMarker,
	String prevMarker
) {}
