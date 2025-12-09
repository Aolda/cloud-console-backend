package com.acc.local.dto.project.quota;

import lombok.Builder;

@Builder
public record ProjectGlobalQuotaDto(
	QuotaInformation instance,
	QuotaInformation core,
	QuotaInformation ram,
	QuotaGroup volume
) {
	public static ProjectGlobalQuotaDto getDefault() {
		return ProjectGlobalQuotaDto.builder()
			.core(QuotaInformation.builder()
				.available(8)
				.build())
			.ram(QuotaInformation.builder()
				.available(32)
				.build())
			.instance(QuotaInformation.builder()
				.available(10)
				.build())
			.volume(QuotaGroup.builder()
				.count(QuotaInformation.builder()
					.available(10)
					.build())
				.size(QuotaInformation.builder()
					.available(1000)
					.build())
				.build())
		.build();
	}
}
