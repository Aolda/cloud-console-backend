package com.acc.local.external.dto;

import lombok.Builder;

@Builder
public record OpenstackPagination(
	boolean isFirst,
	boolean isLast,
	String nextMarker,
	String prevMarker
) {}
