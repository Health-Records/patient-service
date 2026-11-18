package com.health.record.patient_service.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuerUri;

  private final KeycloakRoleConverter keycloakRoleConverter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // Public endpoints
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api/v1/auth/patients/register")
            .permitAll()
            // Patient-specific endpoints
            .requestMatchers("/api/v1/patients/**")
            .hasRole("PATIENT")
            // Patient access and consent management
            .requestMatchers("/api/v1/patient-access/**")
            .hasRole("PATIENT")
            // Platform administration
            .requestMatchers("/api/v1/admin/**")
            .hasRole("PLATFORM_ADMIN")
            // Every other endpoint requires authentication
            .anyRequest()
            .authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {

    NimbusJwtDecoder decoder =
        NimbusJwtDecoder.withIssuerLocation(issuerUri).build();

    // Validates issuer, expiry and other standard timestamp claims
    OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);

    // Validates that the token is intended for Patient Service
    OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {
      if (jwt.getAudience().contains("patient-service")) {
        return OAuth2TokenValidatorResult.success();
      }

      log.warn("JWT audience validation failed. Expected audience: patient-service");
      return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid access token", null));
    };

    decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator));

    return decoder;
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

    converter.setJwtGrantedAuthoritiesConverter(keycloakRoleConverter);

    return converter;
  }
}
