package com.systelab.seed.features.patient.controller.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String coordinates;
    private String street;
    private String city;
    private String zip;
}
