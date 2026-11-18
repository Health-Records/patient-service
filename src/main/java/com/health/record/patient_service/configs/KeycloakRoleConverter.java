package com.health.record.patient_service.configs;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  private static final Set<String> APPLICATION_ROLES =
      Set.of("PATIENT", "DOCTOR", "HOSPITAL_ADMIN", "HOSPITAL_STAFF", "PLATFORM_ADMIN");

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {

    Map<String, Object> realmAccess = jwt.getClaim("realm_access");

    if (realmAccess == null) {
      return List.of();
    }

    List<String> roles = (List<String>) realmAccess.get("roles");

    if (roles == null) {
      return List.of();
    }

    return roles.stream()
        .filter(APPLICATION_ROLES::contains)
        .<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        .toList();
  }
}
