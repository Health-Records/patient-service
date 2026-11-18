package com.health.record.patient_service.dto.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakTokenResponse(
    @JsonProperty("access_token") String accessToken,

    @JsonProperty("expires_in") long expiresIn,

    @JsonProperty("token_type") String tokenType) {}
