package com.health.record.patient_service.repository;

import com.health.record.patient_service.entities.Patient;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

  Optional<Patient> findByKeycloakUserId(String keycloakUserId);

  Optional<Patient> findByEmail(String email);

  Optional<Patient> findByPatientNumber(String patientNumber);
}
