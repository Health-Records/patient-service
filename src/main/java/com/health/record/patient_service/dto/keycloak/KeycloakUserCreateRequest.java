package com.health.record.patient_service.dto.keycloak;

import java.util.List;

public record KeycloakUserCreateRequest(
    String username,
    String email,
    String firstName,
    String lastName,
    boolean enabled,
    boolean emailVerified,
    List<Credential> credentials) {

  public record Credential(String type, String value, boolean temporary) {}
}
