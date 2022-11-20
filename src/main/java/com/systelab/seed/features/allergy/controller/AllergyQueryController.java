package com.systelab.seed.features.allergy.controller;

import com.systelab.seed.features.allergy.controller.dto.AllergyMapper;
import com.systelab.seed.features.allergy.controller.dto.AllergyResponseDTO;
import com.systelab.seed.features.allergy.service.query.AllergyQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Tag(name = "Allergy")
@RequiredArgsConstructor
@RestController()
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class AllergyQueryController {

    private final AllergyQueryService allergyQueryService;
    private final AllergyMapper allergyMapper;

    @Operation(description = "Get all Allergies")
    @PageableAsQueryParam
    @SecurityRequirement(name = "Authorization")
    @GetMapping("allergies")
    public ResponseEntity<Page<AllergyResponseDTO>> getAllAllergies(@Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(this.allergyQueryService.getAllAllergies(pageable).map(allergyMapper::toResponseDTO));
    }

    @Operation(description = "Get an Allergy")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("allergies/{uid}")
    public ResponseEntity<AllergyResponseDTO> getAllergy(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(allergyMapper.toResponseDTO(this.allergyQueryService.getAllergy(id)));
    }
}