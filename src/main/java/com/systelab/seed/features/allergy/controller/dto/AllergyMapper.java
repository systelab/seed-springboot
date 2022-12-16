package com.systelab.seed.features.allergy.controller.dto;

import com.systelab.seed.features.allergy.model.Allergy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AllergyMapper {

    Allergy fromRequestDTO(AllergyRequestDTO dto);

    AllergyResponseDTO toResponseDTO(Allergy allergy);
}
