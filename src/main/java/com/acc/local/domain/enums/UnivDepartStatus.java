package com.acc.local.domain.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UnivDepartStatus {
	ATTENDING(List.of("SS0001","GS0001","PS0001","PS0002","ES0001","ES0002","ES0003","ES0004", "TC0004")),
	COMPLETED(List.of("SS0007", "GS0004")),
	GRADUATED(List.of("SS0002", "GS0002")),
	EXPELLED(List.of("SS0003", "GS0003")),
	UNKNOWN(List.of());

	private final List<String> departCodes;

	public static UnivDepartStatus getUnivDepartStatus(String departType) {
		Optional<UnivDepartStatus> matchedType = Arrays.stream(UnivDepartStatus.values())
			.filter(v -> v.departCodes.contains(departType))
			.findFirst();

		return matchedType.orElse(UnivDepartStatus.UNKNOWN);
	}
}
