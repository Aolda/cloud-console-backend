package com.acc.local.external.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record KeycloakIntrospectResponse(
        @JsonProperty("active") boolean active,
        @JsonProperty("sub") String sub,
        @JsonProperty("exp") Long exp,
        @JsonProperty("iat") Long iat,
        @JsonProperty("client_id") String clientId,
        @JsonProperty("username") String username,
        @JsonProperty("token_type") String tokenType
) {
}
