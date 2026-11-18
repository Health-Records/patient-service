package com.health.record.patient_service.mappers;

import com.health.record.patient_service.dto.PatientRegistrationRequest;
import com.health.record.patient_service.dto.PatientResponse;
import com.health.record.patient_service.entities.Patient;
import com.health.record.patient_service.entities.PatientAddress;
import com.health.record.patient_service.entities.PatientEmergencyContact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "keycloakUserId", ignore = true)
  @Mapping(target = "patientNumber", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "version", ignore = true)
  Patient mapPatientRegistrationRequestToPatientEntity(PatientRegistrationRequest patientRegistrationRequest);

  @Mapping(source = "patientAddress", target = "address")
  @Mapping(source = "patient.phone", target = "phone")
  @Mapping(source = "patient.id", target = "id")
  @Mapping(source = "patientEmergencyContact", target = "emergencyContact")
  @Mapping(source = "patient.createdAt", target = "createdAt")
  @Mapping(source = "patient.updatedAt", target = "updatedAt")
  PatientResponse mapPatientEntityToPatientResponse(
      Patient patient, PatientAddress patientAddress, PatientEmergencyContact patientEmergencyContact);
}
