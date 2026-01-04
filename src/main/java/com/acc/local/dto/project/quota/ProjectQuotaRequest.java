package com.acc.local.dto.project.quota;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ProjectQuotaRequest(
	@Deprecated() // TODO: 'core'로 변경
	@Schema(description = "vCPU 할당량", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
	int vCpu,

	@Deprecated() // TODO: 'ram'로 변경
	@Schema(description = "RAM 할당량(GB)", requiredMode = Schema.RequiredMode.REQUIRED, example = "32")
	int vRam,

	@Deprecated() // TODO: 'volume'로 변경
	@Schema(description = "스토리지 할당량(GB)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
	int storage,

	@Schema(description = "인스턴스 최대 개수", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
	int instance
) {}
