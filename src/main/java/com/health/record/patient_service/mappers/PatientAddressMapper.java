package com.health.record.patient_service.mappers;

import com.health.record.patient_service.dto.Address;
import com.health.record.patient_service.entities.PatientAddress;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientAddressMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "patientId", source = "patientId")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "version", ignore = true)
  PatientAddress mapAddressToPatientAddress(Address address, UUID patientId);
}
