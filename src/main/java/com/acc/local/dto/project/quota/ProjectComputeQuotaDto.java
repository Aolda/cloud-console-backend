package com.acc.local.dto.project.quota;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProjectComputeQuotaDto(
	@Schema(description = "인스턴스 가용/사용량 정보 (단위: 개)") QuotaInformation instance,
	@Schema(description = "CPU 가용/사용량 정보 (단위: 개)") QuotaInformation core,
	@Schema(description = "RAM 가용/사용량 정보 (단위: MB)") QuotaInformation ram,
	@Schema(description = "키페어 가용/사용량 정보 (단위: 개)") QuotaInformation keypair
) {}
