package com.systelab.seed.features.patient.allergy.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class AllergyResponseDTO {
    private UUID id;

    @Schema(description = "Allergy name", example = "Kiwi")
    private String name;

    @Schema(description = "Objective evidence of disease", example = "Bloody nose")
    private String signs;

    @Schema(description = "Subjective evidence of disease", example = "Anxiety, pain and fatigue")
    private String symptoms;
}
