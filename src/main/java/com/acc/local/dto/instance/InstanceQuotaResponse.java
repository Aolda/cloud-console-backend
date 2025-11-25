package com.acc.local.dto.instance;

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

	@Schema(description = "인스턴스 가용량") private QuotaInformation instance;
	@Schema(description = "CPU 가용량") private QuotaInformation core;
	@Schema(description = "RAM 가용량") private QuotaInformation ram;
	@Schema(description = "키페어 가용량") private QuotaInformation keypair;

	@Builder @Getter
	@NoArgsConstructor @AllArgsConstructor
	public static class QuotaInformation {
		int available;
		int used;
	}
}
