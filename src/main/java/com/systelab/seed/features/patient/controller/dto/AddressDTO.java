package com.systelab.seed.features.patient.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AddressDTO {
    @Schema(description = "Coordinates", example = "08021")
    private String coordinates;
    @Schema(description = "Street", example = "Oxford Ave, 12")
    private String street;
    @Schema(description = "City", example = "London")
    private String city;
    @Schema(description = "ZIP", example = "08021")
    private String zip;
}
