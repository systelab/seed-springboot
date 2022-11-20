package com.systelab.seed.features.patient.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Data
public class PatientRequestDTO {

    @NotNull
    @Size(min = 1, max = 255)
    private String surname;

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @Size(max = 255)
    private String medicalNumber;

    private String email;

    @Schema(description = "Date of Birth", example = "1966-11-17")
    private LocalDate dob;

    private AddressDTO address;

}
