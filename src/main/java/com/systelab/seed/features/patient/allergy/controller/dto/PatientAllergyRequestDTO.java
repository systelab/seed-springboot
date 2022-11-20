package com.systelab.seed.features.patient.allergy.controller.dto;

import com.systelab.seed.features.allergy.model.Allergy;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class PatientAllergyRequestDTO {

    private Allergy allergy;

    @Schema(description = "Last date when the person hast the symptoms", example = "2018-05-14")
    private LocalDate lastOccurrence;

    @Schema(description = "Date when the allergy was verified", example = "2007-03-23")
    private LocalDate assertedDate;

    @Size(min = 1, max = 255)
    @NotNull
    @Schema(description = "Relevant notes to take into consideration", example = "Some notes")
    private String note;
}
