package com.acc.local.domain.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UnivAccountType {
	UNDERGRADUATE(List.of("SS")),
	GRADUATED(List.of("GS")),
	PROFESSOR(List.of("PS")),
	EMPLOYEE(List.of("ES", "EM")),
	UNKNOWN(List.of());

	private final List<String> accountPrefixes;

	public static UnivAccountType getType(String departType) {
		String typePrefix = departType.substring(0, 2);
		Optional<UnivAccountType> matchedType = Arrays.stream(UnivAccountType.values())
			.filter(v -> v.accountPrefixes.contains(typePrefix))
			.findFirst();

		return matchedType.orElse(UnivAccountType.UNKNOWN);
	}
}
