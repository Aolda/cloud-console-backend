package com.acc.local.dto.project.quota;

import lombok.Builder;

@Builder
public record QuotaInformation(
	int available,
	int used
) {}
