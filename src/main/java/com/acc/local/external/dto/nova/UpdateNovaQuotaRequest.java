package com.acc.local.external.dto.nova;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateNovaQuotaRequest {

	@JsonProperty("quota_set")
	private QuotaClassSet quotaSet;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class QuotaClassSet {
		private Integer cores;
		private Integer ram;
	}

	@Builder
	public UpdateNovaQuotaRequest(int cores, int ram) {
		this.quotaSet = QuotaClassSet.builder()
			.cores(cores)
			.ram(ram)
			.build();
	}
}
