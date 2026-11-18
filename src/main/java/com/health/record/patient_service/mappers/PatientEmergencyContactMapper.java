package com.health.record.patient_service.mappers;

import com.health.record.patient_service.dto.EmergencyContact;
import com.health.record.patient_service.entities.PatientEmergencyContact;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientEmergencyContactMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "patientId", source = "patientId")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "version", ignore = true)
  PatientEmergencyContact mapEmergencyContactToPatientEmergencyContactEntity(
      EmergencyContact contact, UUID patientId);
}
