package com.acc.local.dto.project.quota;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ProjectStorageQuotaDto(
	@Schema(description = "일반 볼륨저장소 가용/사용량 정보") QuotaGroup volume,
	@Schema(description = "백업본 볼륨저장소 가용/사용량 정보") QuotaGroup backup,
	@Schema(description = "스냅샷 볼륨저장소 가용/사용량 정보 (단위: 개)") QuotaInformation snapshotCount
) {
	public static ProjectStorageQuotaDto from(
		QuotaInformation volumeCount, QuotaInformation volumeSize,
		QuotaInformation backupCount, QuotaInformation backupSize,
		QuotaInformation snapshotCount
	) {
		return ProjectStorageQuotaDto.builder()
			.volume(QuotaGroup.builder()
				.count(volumeCount)
				.size(volumeSize)
				.build())
			.backup(QuotaGroup.builder()
				.count(backupCount)
				.size(backupSize)
				.build())
			.snapshotCount(snapshotCount)
			.build();
	}
}
