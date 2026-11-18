package com.health.record.patient_service.services;

import com.health.record.patient_service.dto.keycloak.KeycloakRoleRepresentation;
import com.health.record.patient_service.dto.keycloak.KeycloakTokenResponse;
import com.health.record.patient_service.dto.keycloak.KeycloakUserCreateRequest;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class KeycloakAdminService {

  private final RestClient restClient;
  private final KeycloakAdminProperties keycloakAdminProperties;

  public KeycloakAdminService(
      @Qualifier("keycloakRestClient") RestClient restClient, KeycloakAdminProperties keycloakAdminProperties) {
    this.restClient = restClient;
    this.keycloakAdminProperties = keycloakAdminProperties;
  }

  public String getAdminAccessToken() {

    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

    formData.add("grant_type", "password");
    formData.add("client_id", keycloakAdminProperties.masterClientId());
    formData.add("username", keycloakAdminProperties.masterUsername());
    formData.add("password", keycloakAdminProperties.masterPassword());

    KeycloakTokenResponse response = restClient
        .post()
        .uri("/realms/{realm}/protocol/openid-connect/token", keycloakAdminProperties.masterRealm())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(formData)
        .retrieve()
        .body(KeycloakTokenResponse.class);

    return Objects.requireNonNull(response, "Keycloak returned an empty token response")
        .accessToken();
  }

  public String createUser(String email, String firstName, String lastName, String password) {

    log.debug("Creating Keycloak user for email: {}", email);

    String accessToken = getAdminAccessToken();

    if (accessToken == null || accessToken.isBlank()) {
      throw new IllegalStateException("Keycloak admin access token is empty");
    }

    KeycloakUserCreateRequest request = new KeycloakUserCreateRequest(
        email,
        email,
        firstName,
        lastName,
        true,
        false,
        List.of(new KeycloakUserCreateRequest.Credential("password", password, false)));

    return restClient
        .post()
        .uri("/admin/realms/{realm}/users", keycloakAdminProperties.realm())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(request)
        .exchange((clientRequest, clientResponse) -> {
          log.info("Keycloak request URL: {}", clientRequest.getURI());

          log.info(
              "Authorization header present: {}",
              clientRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

          log.info(
              "Authorization uses Bearer scheme: {}",
              clientRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION) != null
                  && clientRequest
                      .getHeaders()
                      .getFirst(HttpHeaders.AUTHORIZATION)
                      .startsWith("Bearer "));

          log.info("Keycloak response status: {}", clientResponse.getStatusCode());

          log.info(
              "Keycloak response body: {}",
              org.springframework.util.StreamUtils.copyToString(
                  clientResponse.getBody(), java.nio.charset.StandardCharsets.UTF_8));

          if (!clientResponse.getStatusCode().isSameCodeAs(HttpStatus.CREATED)) {
            throw new IllegalStateException(
                "Keycloak user creation failed: " + clientResponse.getStatusCode());
          }

          String location = clientResponse.getHeaders().getFirst(HttpHeaders.LOCATION);

          if (location == null || location.isBlank()) {
            throw new IllegalStateException("Keycloak created the user but did not return its location");
          }

          return location.substring(location.lastIndexOf('/') + 1);
        });
  }

  public void assignPatientRole(String userId) {

    String accessToken = getAdminAccessToken();

    KeycloakRoleRepresentation patientRole = restClient
        .get()
        .uri("/admin/realms/{realm}/roles/{roleName}", keycloakAdminProperties.realm(), "PATIENT")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .retrieve()
        .body(KeycloakRoleRepresentation.class);

    if (patientRole == null || patientRole.id() == null) {
      throw new IllegalStateException("PATIENT realm role was not found in Keycloak");
    }

    restClient
        .post()
        .uri(
            "/admin/realms/{realm}/users/{userId}/role-mappings/realm",
            keycloakAdminProperties.realm(),
            userId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(List.of(patientRole))
        .retrieve()
        .toBodilessEntity();
  }

  public void deleteUser(String userId) {

    String accessToken = getAdminAccessToken();

    restClient
        .delete()
        .uri("/admin/realms/{realm}/users/{userId}", keycloakAdminProperties.realm(), userId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .retrieve()
        .toBodilessEntity();
  }

  @ConfigurationProperties(prefix = "keycloak.admin")
  public record KeycloakAdminProperties(
      String serverUrl,
      String realm,
      String clientId,
      String clientSecret,
      String masterRealm,
      String masterClientId,
      String masterUsername,
      String masterPassword) {}
}
