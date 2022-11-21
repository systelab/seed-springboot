package com.systelab.seed.features.allergy.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AllergyRequestDTO {
    @NotNull
    @Size(min = 1, max = 255)
    @Schema(description = "Allergy name", example = "Kiwi")
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(description = "Objective evidence of disease", example = "Bloody nose")
    private String signs;

    @Size(min = 1, max = 255)
    @Schema(description = "Subjective evidence of disease", example = "Anxiety, pain and fatigue")
    private String symptoms;
}
