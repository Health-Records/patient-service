package com.health.record.patient_service.services;

import com.health.record.patient_service.dto.PatientRegistrationRequest;
import com.health.record.patient_service.dto.PatientResponse;
import com.health.record.patient_service.entities.Patient;
import com.health.record.patient_service.entities.PatientAddress;
import com.health.record.patient_service.entities.PatientEmergencyContact;
import com.health.record.patient_service.mappers.PatientAddressMapper;
import com.health.record.patient_service.mappers.PatientEmergencyContactMapper;
import com.health.record.patient_service.mappers.PatientMapper;
import com.health.record.patient_service.repository.PatientAddressRepository;
import com.health.record.patient_service.repository.PatientEmergencyContactRepository;
import com.health.record.patient_service.repository.PatientRepository;
import com.health.record.patient_service.utils.PatientNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class PatientService {

  private final PatientRepository patientRepository;
  private final PatientAddressRepository patientAddressRepository;
  private final PatientEmergencyContactRepository emergencyContactRepository;

  private final PatientMapper patientMapper;
  private final PatientAddressMapper addressMapper;
  private final PatientEmergencyContactMapper emergencyContactMapper;

  private final KeycloakAdminService keycloakAdminService;
  private final PatientNumberGenerator patientNumberGenerator;
  private final TransactionTemplate transactionTemplate;

  public PatientResponse registerPatient(PatientRegistrationRequest patientRegistrationRequest) {

    validateRegistration(patientRegistrationRequest);

    String keycloakUserId = createKeycloakUser(patientRegistrationRequest);

    RegisteredPatientRecord registeredPatientRecord;
    try {
      assignPatientRole(keycloakUserId);

      registeredPatientRecord = persistPatient(patientRegistrationRequest, keycloakUserId);

    } catch (RuntimeException exception) {
      compensateKeycloakUser(keycloakUserId, exception);
      throw exception;
    }

    return patientMapper.mapPatientEntityToPatientResponse(
        registeredPatientRecord.patient(),
        registeredPatientRecord.address(),
        registeredPatientRecord.emergencyContact());
  }

  // Validate duplicate email
  private void validateRegistration(PatientRegistrationRequest request) {

    if (patientRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new IllegalStateException("A patient with this email already exists");
    }
  }

  // Create Keycloak identity
  private String createKeycloakUser(PatientRegistrationRequest request) {

    return keycloakAdminService.createUser(
        request.getEmail(), request.getFirstName(), request.getLastName(), request.getPassword());
  }

  // Assign PATIENT role
  private void assignPatientRole(String keycloakUserId) {

    keycloakAdminService.assignPatientRole(keycloakUserId);
  }

  // Persist patient and related information
  private RegisteredPatientRecord persistPatient(PatientRegistrationRequest request, String keycloakUserId) {

    return transactionTemplate.execute(status -> {
      Patient patient = patientMapper.mapPatientRegistrationRequestToPatientEntity(request);

      patient.setKeycloakUserId(keycloakUserId);
      patient.setPatientNumber(generateUniquePatientNumber());

      Patient savedPatient = patientRepository.save(patient);

      PatientAddress savePatientAddress = savePatientAddress(request, savedPatient);
      PatientEmergencyContact savePatientEmergencyContact = saveEmergencyContact(request, savedPatient);

      return new RegisteredPatientRecord(savedPatient, savePatientAddress, savePatientEmergencyContact);
    });
  }

  // Generate a unique patient number
  private String generateUniquePatientNumber() {

    String patientNumber;

    do {
      patientNumber = patientNumberGenerator.generate();
    } while (patientRepository.findByPatientNumber(patientNumber).isPresent());

    return patientNumber;
  }

  // Save address if provided
  private PatientAddress savePatientAddress(PatientRegistrationRequest request, Patient patient) {

    if (request.getAddress() != null) {

      var address = addressMapper.mapAddressToPatientAddress(request.getAddress(), patient.getId());

      return patientAddressRepository.save(address);
    }
    return null;
  }

  // Save emergency contact if provided
  private PatientEmergencyContact saveEmergencyContact(PatientRegistrationRequest request, Patient patient) {

    if (request.getEmergencyContact() != null) {

      var emergencyContact = emergencyContactMapper.mapEmergencyContactToPatientEmergencyContactEntity(
          request.getEmergencyContact(), patient.getId());

      return emergencyContactRepository.save(emergencyContact);
    }
    return null;
  }

  // Compensation if registration fails
  private void compensateKeycloakUser(String keycloakUserId, RuntimeException originalException) {

    try {
      keycloakAdminService.deleteUser(keycloakUserId);

    } catch (RuntimeException compensationException) {

      originalException.addSuppressed(compensationException);
    }
  }

  public record RegisteredPatientRecord(
      Patient patient, PatientAddress address, PatientEmergencyContact emergencyContact) {}
}
