package com.health.record.patient_service.repository;

import com.health.record.patient_service.entities.PatientAddress;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientAddressRepository extends JpaRepository<PatientAddress, UUID> {

  Optional<PatientAddress> findByPatientId(UUID patientId);
}
