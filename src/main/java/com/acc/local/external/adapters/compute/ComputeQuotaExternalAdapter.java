package com.acc.local.external.adapters.compute;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.acc.global.exception.compute.ComputeErrorCode;
import com.acc.global.exception.compute.ComputeException;
import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.external.dto.nova.UpdateNovaQuotaRequest;
import com.acc.local.external.modules.nova.NovaQuotaSetAPIModule;
import com.acc.local.external.ports.compute.ComputeQuotaExternalPort;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ComputeQuotaExternalAdapter implements ComputeQuotaExternalPort {

	private final NovaQuotaSetAPIModule novaQuotaSetAPIModule;

	@Override
	public ResponseEntity<JsonNode> callGetQuota(String token, String projectId) {
		try {
			return novaQuotaSetAPIModule.showQuota(token, projectId);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.ACCEPTED || status == HttpStatus.NO_CONTENT) {
				return ResponseEntity.status(status).build();
			}
			if (status == HttpStatus.NOT_FOUND) {
				throw new ComputeException(ComputeErrorCode.COMPUTE_NOT_FOUND, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new ComputeException(ComputeErrorCode.FORBIDDEN_ACCESS, e);
			}
			throw new ComputeException(ComputeErrorCode.NOVA_API_FAILURE, e);
		}
	}

	@Override
	public ResponseEntity<Void> callUpdateCPUQuota(String token, String projectId, int cpuLimit) {
		try {
			UpdateNovaQuotaRequest quotaRequest = UpdateNovaQuotaRequest.builder()
				.cores(cpuLimit)
				.build();

			ResponseEntity<JsonNode> response = novaQuotaSetAPIModule.updateQuota(token, projectId, quotaRequest);
			return ResponseEntity.status(response.getStatusCode()).build();

		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.ACCEPTED || status == HttpStatus.NO_CONTENT) {
				return ResponseEntity.status(status).build();
			}
			if (status == HttpStatus.NOT_FOUND) {
				throw new ComputeException(ComputeErrorCode.COMPUTE_NOT_FOUND, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new ComputeException(ComputeErrorCode.FORBIDDEN_ACCESS, e);
			}
			throw new ComputeException(ComputeErrorCode.NOVA_API_FAILURE, e);
		}
	}

	@Override
	public ResponseEntity<Void> callUpdateRAMQuota(String token, String projectId, int ramLimit) {
		try {
			UpdateNovaQuotaRequest quotaRequest = UpdateNovaQuotaRequest.builder()
				.ram(ramLimit)
				.build();

			ResponseEntity<JsonNode> response = novaQuotaSetAPIModule.updateQuota(token, projectId, quotaRequest);
			return ResponseEntity.status(response.getStatusCode()).build();

		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.ACCEPTED || status == HttpStatus.NO_CONTENT) {
				return ResponseEntity.status(status).build();
			}
			if (status == HttpStatus.NOT_FOUND) {
				throw new ComputeException(ComputeErrorCode.COMPUTE_NOT_FOUND, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new ComputeException(ComputeErrorCode.FORBIDDEN_ACCESS, e);
			}
			throw new ComputeException(ComputeErrorCode.NOVA_API_FAILURE, e);
		}
	}

	@Override
	public ResponseEntity<Void> callUpdateCPUAndRAMQuota(String token, String projectId, int cpuLimit, int ramLimit) {
		try {
			UpdateNovaQuotaRequest quotaRequest = UpdateNovaQuotaRequest.builder()
				.cores(cpuLimit)
				.ram(ramLimit)
				.build();

			ResponseEntity<JsonNode> response = novaQuotaSetAPIModule.updateQuota(token, projectId, quotaRequest);
			return ResponseEntity.status(response.getStatusCode()).build();

		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.ACCEPTED || status == HttpStatus.NO_CONTENT) {
				return ResponseEntity.status(status).build();
			}
			if (status == HttpStatus.NOT_FOUND) {
				throw new ComputeException(ComputeErrorCode.COMPUTE_NOT_FOUND, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new ComputeException(ComputeErrorCode.FORBIDDEN_ACCESS, e);
			}
			throw new ComputeException(ComputeErrorCode.NOVA_API_FAILURE, e);
		}
	}

}
