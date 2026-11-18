package com.health.record.patient_service.controllers;

import com.health.record.patient_service.api.PatientApi;
import com.health.record.patient_service.dto.PatientRegistrationRequest;
import com.health.record.patient_service.dto.PatientResponse;
import com.health.record.patient_service.services.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PatientController implements PatientApi {

  private final PatientService patientService;

  @Override
  public ResponseEntity<PatientResponse> registerPatient(
      @Valid PatientRegistrationRequest patientRegistrationRequest) {

    log.debug("Patient Request : {}", patientRegistrationRequest);
    PatientResponse patientResponse = patientService.registerPatient(patientRegistrationRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(patientResponse);
  }
}
