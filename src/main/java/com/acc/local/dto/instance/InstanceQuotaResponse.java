package com.acc.local.dto.instance;

import com.acc.local.dto.project.quota.ProjectComputeQuotaDto;
import com.acc.local.dto.project.quota.QuotaInformation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanceQuotaResponse {
	@Schema(description = "인스턴스 사용량 및 한도 (단위: 개)", example = "{\"usage\": 3, \"limit\": 10}")
	private QuotaInformation instance;

	@Schema(description = "vCPU 사용량 및 한도 (단위: 개)", example = "{\"usage\": 8, \"limit\": 20}")
	private QuotaInformation core;

	@Schema(description = "RAM 사용량 및 한도 (단위: MB)", example = "{\"usage\": 16384, \"limit\": 51200}")
	private QuotaInformation ram;

	@Schema(description = "키페어 사용량 및 한도 (단위: 개)", example = "{\"usage\": 2, \"limit\": 100}")
	private QuotaInformation keypair;

	public static InstanceQuotaResponse from(ProjectComputeQuotaDto quotaDto) {
		return InstanceQuotaResponse.builder()
			.instance(quotaDto.instance())
			.core(quotaDto.core())
			.ram(quotaDto.ram())
			.keypair(quotaDto.keypair())
			.build();
	}
}
