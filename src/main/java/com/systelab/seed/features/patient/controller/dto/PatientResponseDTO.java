package com.systelab.seed.features.patient.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PatientResponseDTO {

    @Schema(description = "Internal ID", example = "30649644-80ce-42eb-816f-7b730b9eddce")
    private UUID id;
    @Schema(description = "Surname", example = "Barrows")
    private String surname;
    @Schema(description = "Name", example = "John")
    private String name;
    @Schema(description = "History Number", example = "ASAS323232356743")
    private String medicalNumber;
    @Schema(description = "Mail", example = "john@google.com")
    private String email;
    @Schema(description = "Date of Birth", example = "1966-11-17")
    private LocalDate dob;
    private AddressDTO address;
}
