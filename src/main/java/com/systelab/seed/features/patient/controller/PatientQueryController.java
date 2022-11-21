package com.systelab.seed.features.patient.controller;

import com.systelab.seed.features.patient.controller.dto.PatientMapper;
import com.systelab.seed.features.patient.controller.dto.PatientResponseDTO;
import com.systelab.seed.features.patient.service.PatientService;
import com.systelab.seed.features.patient.service.query.PatientQueryService;
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

@Tag(name = "Patient")
@RestController()
@RequiredArgsConstructor
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientQueryController {

    private final PatientQueryService patientQueryService;
    private final PatientMapper patientMapper;

    @Operation(description = "Get all Patients")
    @PageableAsQueryParam
    @SecurityRequirement(name = "Authorization")
    @GetMapping("patients")
    public ResponseEntity<Page<PatientResponseDTO>> getAllPatients(@Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(this.patientQueryService.getAllPatients(pageable).map(patientMapper::toResponseDTO));
    }

    @Operation(description = "Get Patient")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("patients/{uid}")
    public ResponseEntity<PatientResponseDTO> getPatient(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(patientMapper.toResponseDTO(this.patientQueryService.getPatient(id)));
    }
}