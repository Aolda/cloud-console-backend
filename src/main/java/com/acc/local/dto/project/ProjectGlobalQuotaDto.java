package com.acc.local.dto.project;

import lombok.Builder;

@Builder
public record ProjectGlobalQuotaDto(
	int vCpu,
	int vRam,
	int instance,
	int storage
) {
	public static ProjectGlobalQuotaDto getDefault() {
		return ProjectGlobalQuotaDto.builder()
			.vCpu(8)
			.vRam(32)
			.instance(10)
			.storage(1000)
		.build();
	}
}
