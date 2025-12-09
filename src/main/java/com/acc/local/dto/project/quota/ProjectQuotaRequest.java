package com.acc.local.dto.project.quota;

import lombok.Builder;

@Builder
public record ProjectQuotaRequest(
	@Deprecated() // TODO: 'core'로 변경
	int vCpu,
	@Deprecated() // TODO: 'ram'로 변경
	int vRam,
	@Deprecated() // TODO: 'volume'로 변경
	int storage,
	int instance
) {}
