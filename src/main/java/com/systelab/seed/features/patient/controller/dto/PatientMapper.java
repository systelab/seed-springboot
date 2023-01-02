package com.systelab.seed.features.patient.controller.dto;

import com.systelab.seed.features.patient.model.Patient;
import org.mapstruct.Mapper;

@Mapper
public interface PatientMapper {

    Patient fromRequestDTO(PatientRequestDTO dto);

    PatientResponseDTO toResponseDTO(Patient user);
}
