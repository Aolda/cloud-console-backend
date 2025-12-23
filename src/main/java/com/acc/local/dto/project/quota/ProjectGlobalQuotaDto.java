package com.acc.local.dto.project.quota;

import lombok.Builder;

public record ProjectGlobalQuotaDto(
	QuotaInformation instance,
	QuotaInformation core,
	QuotaInformation ram,
	QuotaGroup volume
) {

	private static final QuotaInformation baseCoreQuota = QuotaInformation.builder().available(8).build();
	private static final QuotaInformation baseRamQuota = QuotaInformation.builder().available(32).build();
	private static final QuotaInformation baseInstanceQuota = QuotaInformation.builder().available(10).build();
	private static final QuotaGroup baseVolumeQuotaGroup = QuotaGroup.builder()
		.count(QuotaInformation.builder().available(10).build())
		.size(QuotaInformation.builder().available(1000).build())
		.build();

	@Builder
	public ProjectGlobalQuotaDto {
		if (instance == null) {
			instance = baseInstanceQuota;
		}
		if (core == null) {
			core = baseCoreQuota;
		}
		if (ram == null) {
			ram = baseRamQuota;
		}
		if (volume == null) {
			volume = baseVolumeQuotaGroup;
		}
	}

	public static ProjectGlobalQuotaDto getDefault() {
		return ProjectGlobalQuotaDto.builder().build();
	}
}
