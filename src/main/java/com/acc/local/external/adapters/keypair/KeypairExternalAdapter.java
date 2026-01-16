package com.acc.local.external.adapters.keypair;

import com.acc.global.exception.keypair.KeypairExternalErrorCode;
import com.acc.global.exception.keypair.KeypairExternalException;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
import com.acc.local.dto.keypair.KeypairSyncDto;
import com.acc.local.external.dto.nova.keypair.CreateKeyPairRequest;
import com.acc.local.external.modules.nova.NovaKeypairAPIModule;
import com.acc.local.external.ports.KeypairExternalPort;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeypairExternalAdapter implements KeypairExternalPort {

    private final NovaKeypairAPIModule novaKeypairAPIModule;

    @Override
    public CreateKeypairResponse createKeypair(String keystoneToken, CreateKeypairRequest request) {

        CreateKeyPairRequest.KeyPair keyPairPayload = CreateKeyPairRequest.KeyPair.builder()
                .name(request.getKeypairName())
                .build();

        CreateKeyPairRequest apiRequest = CreateKeyPairRequest.builder()
                .keypair(keyPairPayload)
                .build();

        ResponseEntity<JsonNode> response;
        try {
            response = novaKeypairAPIModule.createKeyPair(keystoneToken, apiRequest);

        } catch (WebClientException e) {
            if (e instanceof WebClientResponseException ex) {
                if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                    throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_ALREADY_EXISTS);
                }
                if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    log.error(ex.getResponseBodyAsString());
                    throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_INVALID_REQUEST);
                }
                if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_UNAUTHORIZED);
                }
                if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                    throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_FORBIDDEN);
                }
            }
            throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_CREATION_FAILED);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_CREATION_FAILED);
        }

        return CreateKeypairResponse.builder()
                .keypairName(response.getBody().get("keypair").get("name").asText())
                .fingerprint(response.getBody().get("keypair").get("fingerprint").asText())
                .publicKey(response.getBody().get("keypair").get("public_key").asText())
                .privateKey(response.getBody().get("keypair").get("private_key").asText())
                .build();
    }

    @Override
    public void deleteKeypair(String keystoneToken, String keypairName) {
        try {
            novaKeypairAPIModule.deleteKeyPair(keystoneToken, keypairName);
        } catch (WebClientException e) {
            if (e instanceof WebClientResponseException ex) {
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_NOT_FOUND);
                }
            }
            throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_DELETION_FAILED);
        }
    }

    @Override
    public List<KeypairSyncDto> listKeypairsByUser(String keystoneToken) {
        List<KeypairSyncDto> allKeypairs = new ArrayList<>();
        String marker = null;
        boolean hasMore = true;

        try {
            while (hasMore) {
                Map<String, String> queryParams = new HashMap<>();
                queryParams.put("limit", "100");  // 한 번에 100개씩
                if (marker != null) {
                    queryParams.put("marker", marker);
                }

                ResponseEntity<JsonNode> response = novaKeypairAPIModule.listKeyPairs(keystoneToken, queryParams);
                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    throw new KeypairExternalException(
                        KeypairExternalErrorCode.KEYPAIR_EXTERNAL_LIST_FAILED
                    );
                }

                JsonNode body = response.getBody();
                JsonNode keypairs = body.get("keypairs");

                if (keypairs != null && keypairs.isArray()) {
                    for (JsonNode item : keypairs) {
                        JsonNode keypair = item.get("keypair");

                        KeypairSyncDto dto = KeypairSyncDto.builder()
                            .name(keypair.get("name").asText())
                            .fingerprint(keypair.get("fingerprint").asText())
                            .build();

                        allKeypairs.add(dto);
                        marker = dto.getName();  // 다음 페이지를 위한 마커
                    }
                }

                // keypairs_links가 없거나 비어있으면 마지막 페이지
                JsonNode links = body.get("keypairs_links");
                hasMore = (links != null && links.isArray() && links.size() > 0);
            }
            return allKeypairs;

        } catch (WebClientException e) {
            if (e instanceof WebClientResponseException ex) {
                if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_UNAUTHORIZED);
                }
                if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                    throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_FORBIDDEN);
                }
                log.error("Failed to list keypairs: {}", ex.getResponseBodyAsString());
            }
            throw new KeypairExternalException(KeypairExternalErrorCode.KEYPAIR_EXTERNAL_LIST_FAILED);
        }
    }
}
