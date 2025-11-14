package com.acc.local.external.adapters.keypair;

import com.acc.global.exception.keypair.KeypairExternalErrorCode;
import com.acc.global.exception.keypair.KeypairExternalException;
import com.acc.local.dto.keypair.CreateKeypairRequest;
import com.acc.local.dto.keypair.CreateKeypairResponse;
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
}
