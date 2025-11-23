package com.acc.local.dto.project;

import lombok.Builder;

@Builder
public record ProjectQuotaDto(
	int vCpu,
	int vRam,
	int instance,
	int storage
) {
	public static ProjectQuotaDto getDefault() {
		return ProjectQuotaDto.builder()
			.vCpu(8)
			.vRam(32)
			.instance(10)
			.storage(1000)
		.build();
	}
}
