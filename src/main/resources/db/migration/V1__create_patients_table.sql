-- =====================================================
-- PATIENTS TABLE
-- =====================================================

CREATE TABLE patients
(
    -- Primary key
    id UUID PRIMARY KEY,

    -- Keycloak identity
    keycloak_user_id VARCHAR(255) NOT NULL UNIQUE,

    -- Patient identification
    patient_number VARCHAR(30) NOT NULL UNIQUE,

    -- Personal information
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,

    -- Contact information
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,

    -- Medical information
    blood_group VARCHAR(20),

    -- Terms and conditions
    terms_accepted BOOLEAN NOT NULL,

    -- BaseEntity fields
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT chk_patients_gender
        CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),

    CONSTRAINT chk_patients_blood_group
        CHECK (
            blood_group IS NULL OR blood_group IN (
                'A_POSITIVE',
                'A_NEGATIVE',
                'B_POSITIVE',
                'B_NEGATIVE',
                'AB_POSITIVE',
                'AB_NEGATIVE',
                'O_POSITIVE',
                'O_NEGATIVE'
            )
        ),

    CONSTRAINT chk_patients_terms_accepted
        CHECK (terms_accepted = TRUE)
);


-- =====================================================
-- PATIENT ADDRESSES TABLE
-- =====================================================

CREATE TABLE patient_addresses
(
    -- Primary key
    id UUID PRIMARY KEY,

    -- Patient reference
    patient_id UUID NOT NULL UNIQUE,

    -- Address information
    address_line1 VARCHAR(200) NOT NULL,
    address_line2 VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) NOT NULL,

    -- BaseEntity fields
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign key
    CONSTRAINT fk_patient_address_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE CASCADE
);


-- =====================================================
-- PATIENT EMERGENCY CONTACTS TABLE
-- =====================================================

CREATE TABLE patient_emergency_contacts
(
    -- Primary key
    id UUID PRIMARY KEY,

    -- Patient reference
    patient_id UUID NOT NULL UNIQUE,

    -- Emergency contact information
    name VARCHAR(100) NOT NULL,
    relationship VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,

    -- BaseEntity fields
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,

    -- Foreign key
    CONSTRAINT fk_patient_emergency_contact_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE CASCADE
);