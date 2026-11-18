package com.health.record.patient_service.utils;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class PatientNumberGenerator {

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  private static final int SUFFIX_LENGTH = 8;

  private final SecureRandom secureRandom = new SecureRandom();

  public String generate() {

    StringBuilder suffix = new StringBuilder(SUFFIX_LENGTH);

    for (int i = 0; i < SUFFIX_LENGTH; i++) {
      int index = secureRandom.nextInt(CHARACTERS.length());
      suffix.append(CHARACTERS.charAt(index));
    }

    return "PAT-" + suffix;
  }
}
