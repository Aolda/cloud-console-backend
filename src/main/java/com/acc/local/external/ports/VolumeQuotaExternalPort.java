package com.acc.local.external.ports;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

public interface VolumeQuotaExternalPort {

	ResponseEntity<JsonNode> callGetQuota(String token, String projectId);

	ResponseEntity<Void> callUpdateVolumeQuota(String token, String projectId, int storageLimit);
}
