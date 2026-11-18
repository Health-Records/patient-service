package com.health.record.patient_service.controllers;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthTestController {

  @GetMapping("/me")
  public Map<String, Object> getCurrentUser(Authentication authentication) {

    return Map.of(
        "username", authentication.getName(),
        "authorities", authentication.getAuthorities());
  }

  @GetMapping("/patient")
  public String patientOnly() {
    return "Welcome PATIENT! You have access to this endpoint.";
  }

  @GetMapping("/doctor")
  public String doctorOnly() {
    return "Welcome DOCTOR! You have access to this endpoint.";
  }
}
