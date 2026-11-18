package com.health.record.patient_service.repository;

import com.health.record.patient_service.entities.PatientEmergencyContact;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientEmergencyContactRepository extends JpaRepository<PatientEmergencyContact, UUID> {

  Optional<PatientEmergencyContact> findByPatientId(UUID patientId);
}
