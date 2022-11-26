package com.systelab.seed.features.patient.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;


@Data
public class PatientRequestDTO {

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(description = "Surname", example = "Barrows")
    private String surname;

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(description = "Name", example = "John")
    private String name;

    @Size(max = 255)
    @Schema(description = "History Number", example = "ASAS323232356743")
    private String medicalNumber;

    @Schema(description = "Mail", example = "john@google.com")
    private String email;

    @Schema(description = "Date of Birth", example = "1966-11-17")
    private LocalDate dob;

    @Valid
    private AddressDTO address;

}
