package com.systelab.seed.features.patient.controller;

import com.systelab.seed.features.patient.controller.dto.PatientMapper;
import com.systelab.seed.features.patient.controller.dto.PatientRequestDTO;
import com.systelab.seed.features.patient.controller.dto.PatientResponseDTO;
import com.systelab.seed.features.patient.model.Patient;
import com.systelab.seed.features.patient.service.command.PatientCreationCommandService;
import com.systelab.seed.features.patient.service.command.PatientDeleteCommandService;
import com.systelab.seed.features.patient.service.command.PatientUpdateCommandService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;

@Tag(name = "Patient")
@RestController()
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientCommandController {

    private final PatientCreationCommandService patientCreationCommandService;
    private final PatientUpdateCommandService patientUpdateCommandService;
    private final PatientDeleteCommandService patientDeleteCommandService;
    private final PatientMapper patientMapper;

    private final Counter patientCreatedCounter;

    @Autowired
    public PatientCommandController(PatientCreationCommandService patientCreationCommandService,
                                    PatientUpdateCommandService patientUpdateCommandService,
                                    PatientDeleteCommandService patientDeleteCommandService,
                                    PatientMapper patientMapper, MeterRegistry registry) {
        this.patientCreationCommandService = patientCreationCommandService;
        this.patientUpdateCommandService = patientUpdateCommandService;
        this.patientDeleteCommandService = patientDeleteCommandService;
        this.patientMapper = patientMapper;
        patientCreatedCounter = Counter
                .builder("patients")
                .description("Number of patients created in the application")
                .register(registry);
    }

    @Operation(description = "Create a Patient")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("patients/patient")
    public ResponseEntity<PatientResponseDTO> createPatient(@RequestBody @Parameter(description = "Patient", required = true) @Valid PatientRequestDTO dto) {
        patientCreatedCounter.increment();
        Patient patient = this.patientCreationCommandService.createPatient(patientMapper.fromRequestDTO(dto));
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/patients/{id}").buildAndExpand(patient.getId()).toUri();
        return ResponseEntity.created(uri).body(patientMapper.toResponseDTO(patient));
    }

    @Operation(description = "Create or Update (idempotent) an existing Patient")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("patients/{uid}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable("uid") UUID id, @RequestBody @Parameter(description = "Patient", required = true) @Valid PatientRequestDTO dto) {
        Patient patient = this.patientUpdateCommandService.updatePatient(id, patientMapper.fromRequestDTO(dto));
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(patientMapper.toResponseDTO(patient));
    }

    @Operation(description = "Delete a Patient")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("patients/{uid}")
    public ResponseEntity removePatient(@PathVariable("uid") UUID id) {
        this.patientDeleteCommandService.removePatient(id);
        return ResponseEntity.noContent().build();
    }

}