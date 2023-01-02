package com.systelab.seed.features.patient.allergy.controller.dto;

import com.systelab.seed.features.patient.allergy.model.PatientAllergy;
import org.mapstruct.Mapper;

@Mapper
public interface PatientAllergyMapper {

    PatientAllergy fromRequestDTO(PatientAllergyRequestDTO dto);

    PatientAllergyResponseDTO toResponseDTO(PatientAllergy patientAllergy);
}
