package com.systelab.seed.features.patient.allergy.controller;

import com.systelab.seed.features.patient.allergy.controller.dto.PatientAllergyMapper;
import com.systelab.seed.features.patient.allergy.controller.dto.PatientAllergyResponseDTO;
import com.systelab.seed.features.patient.allergy.service.query.PatientAllergyQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "PatientAllergy")
@RequiredArgsConstructor
@RestController()
@RequestMapping(value = "/seed/v1/patients", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientAllergyQueryController {

    private final PatientAllergyQueryService patientAllergyQueryService;
    private final PatientAllergyMapper patientAllergyMapper;

    @Operation(description = "Get Allergies from Patient")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("{uid}/allergies")
    public ResponseEntity<Set<PatientAllergyResponseDTO>> getPatientAllergies(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.patientAllergyQueryService.getAllergiesFromPatient(id).stream().map(patientAllergyMapper::toResponseDTO).collect(Collectors.toSet()));
    }
}
