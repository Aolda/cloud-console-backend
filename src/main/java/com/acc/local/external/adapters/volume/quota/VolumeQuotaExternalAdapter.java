package com.acc.local.external.adapters.volume.quota;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.acc.global.exception.volume.VolumeErrorCode;
import com.acc.global.exception.volume.VolumeException;
import com.acc.local.external.dto.cinder.quota.UpdateQuotaRequest;
import com.acc.local.external.modules.cinder.CinderQuotasModule;
import com.acc.local.external.ports.VolumeQuotaExternalPort;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VolumeQuotaExternalAdapter implements VolumeQuotaExternalPort {

	private final CinderQuotasModule cinderQuotasModule;

	@Override
	public ResponseEntity<JsonNode> callGetQuota(String token, String projectId) {
		try {
			return cinderQuotasModule.getQuotaSet(token, projectId);
		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.ACCEPTED || status == HttpStatus.NO_CONTENT) {
				return ResponseEntity.status(status).build();
			}
			if (status == HttpStatus.NOT_FOUND) {
				throw new VolumeException(VolumeErrorCode.SNAPSHOT_NOT_FOUND, e);
			} else if (status == HttpStatus.CONFLICT) {
				throw new VolumeException(VolumeErrorCode.SNAPSHOT_IN_USE, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS, e);
			}
			throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
		}
	}

	public ResponseEntity<Void> callUpdateVolumeQuota(String token, String projectId, int storageLimit) {
		try {
			UpdateQuotaRequest quotaRequest = UpdateQuotaRequest.builder()
				.quotaSet(
					UpdateQuotaRequest.QuotaSet.builder()
						.volumes(storageLimit).build()
				)
				.build();

			ResponseEntity<JsonNode> response = cinderQuotasModule.updateQuota(token, projectId, quotaRequest);
			return ResponseEntity.status(response.getStatusCode()).build();

		} catch (WebClientResponseException e) {
			HttpStatusCode status = e.getStatusCode();
			if (status == HttpStatus.ACCEPTED || status == HttpStatus.NO_CONTENT) {
				return ResponseEntity.status(status).build();
			}
			if (status == HttpStatus.NOT_FOUND) {
				throw new VolumeException(VolumeErrorCode.SNAPSHOT_NOT_FOUND, e);
			} else if (status == HttpStatus.CONFLICT) {
				throw new VolumeException(VolumeErrorCode.SNAPSHOT_IN_USE, e);
			} else if (status == HttpStatus.FORBIDDEN) {
				throw new VolumeException(VolumeErrorCode.FORBIDDEN_ACCESS, e);
			}
			throw new VolumeException(VolumeErrorCode.CINDER_API_FAILURE, e);
		}
	}

}
