package com.systelab.seed.features.patient.controller.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PatientResponseDTO {

    private UUID id;
    private String surname;
    private String name;
    private String medicalNumber;
    private String email;
    private LocalDate dob;
    private AddressDTO address;
}
