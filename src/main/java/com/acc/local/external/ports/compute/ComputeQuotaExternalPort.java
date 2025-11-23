package com.acc.local.external.ports.compute;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

public interface ComputeQuotaExternalPort {

	ResponseEntity<JsonNode> callGetQuota(String token, String projectId);

	ResponseEntity<Void> callUpdateCPUQuota(String token, String projectId, int cpuLimit);

	ResponseEntity<Void> callUpdateRAMQuota(String token, String projectId, int ramLimitWithMBUnit);

	ResponseEntity<Void> callUpdateCPUAndRAMQuota(String token, String projectId, int cpuLimit, int ramLimitWithMBUnit);



}
