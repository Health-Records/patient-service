package com.health.record.patient_service.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Bean
  public RestClient.Builder restClientBuilder() {
    return RestClient.builder();
  }

  @Bean
  public RestClient keycloakRestClient(
      RestClient.Builder builder, @Value("${keycloak.admin.server-url}") String serverUrl) {

    return builder.clone().baseUrl(serverUrl).build();
  }
}
