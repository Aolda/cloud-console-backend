package com.acc.local.external.dto.keystone;

import lombok.Builder;

@Builder
public record CreateKeystoneProjectRequest(
	String projectName,
	String projectDescription
) {}
