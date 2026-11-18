package com.health.record.patient_service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patient_addresses")
@Getter
@Setter
@NoArgsConstructor
public class PatientAddress extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "patient_id", nullable = false, unique = true)
  private UUID patientId;

  @Column(name = "address_line1", nullable = false, length = 200)
  private String addressLine1;

  @Column(name = "address_line2", length = 200)
  private String addressLine2;

  @Column(nullable = false, length = 100)
  private String city;

  @Column(length = 100)
  private String state;

  @Column(name = "postal_code", length = 20)
  private String postalCode;

  @Column(nullable = false, length = 100)
  private String country;
}
