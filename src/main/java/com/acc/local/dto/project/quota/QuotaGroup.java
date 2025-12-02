package com.acc.local.dto.project.quota;

import lombok.Builder;

@Builder
public record QuotaGroup (
	QuotaInformation count,
	QuotaInformation size
) {}
