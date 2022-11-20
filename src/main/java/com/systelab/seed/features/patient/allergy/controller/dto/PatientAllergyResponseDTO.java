package com.systelab.seed.features.patient.allergy.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientAllergyResponseDTO {

    private AllergyResponseDTO allergy;

    @Schema(description = "Last date when the person hast the symptoms", example = "2018-05-14")
    private LocalDate lastOccurrence;

    @Schema(description = "Date when the allergy was verified", example = "2007-03-23")
    private LocalDate assertedDate;

    @Schema(description = "Relevant notes to take into consideration", example = "Some notes")
    private String note;
}
