package com.systelab.seed.patient.controller;

import com.systelab.seed.patient.model.Patient;
import com.systelab.seed.patient.service.PatientService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.data.rest.converters.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@Tag(name = "Patient")
@RestController()
// Bad idea to have that in production
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization", allowCredentials = "true")
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientController {

    private final PatientService patientService;

    private final Counter patientCreatedCounter;

    @Autowired
    public PatientController(PatientService patientService, MeterRegistry registry) {
        this.patientService = patientService;
        patientCreatedCounter = Counter
                .builder("patients")
                .description("Number of patients created in the application")
                .register(registry);
    }

    @Operation(description = "Get all Patients")
    @PageableAsQueryParam
    @SecurityRequirement(name = "Authorization")
    @GetMapping("patients")
    public ResponseEntity<Page<Patient>> getAllPatients(@Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(this.patientService.getAllPatients(pageable));
    }

    @Operation(description = "Get Patient")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("patients/{uid}")
    public ResponseEntity<Patient> getPatient(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.patientService.getPatient(id));
    }

    @Operation(description = "Create a Patient")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("patients/patient")
    public ResponseEntity<Patient> createPatient(@RequestBody @Parameter(description = "Patient", required = true) @Valid Patient p) {
        patientCreatedCounter.increment();
        Patient patient = this.patientService.createPatient(p);
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/patients/{id}").buildAndExpand(patient.getId()).toUri();
        return ResponseEntity.created(uri).body(patient);
    }

    @Operation(description = "Create or Update (idempotent) an existing Patient")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("patients/{uid}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("uid") UUID id, @RequestBody @Parameter(description = "Patient", required = true) @Valid Patient p) {
        Patient patient = this.patientService.updatePatient(id, p);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(patient);
    }

    @Operation(description = "Delete a Patient")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("patients/{uid}")
    public ResponseEntity removePatient(@PathVariable("uid") UUID id) {
        this.patientService.removePatient(id);
        return ResponseEntity.noContent().build();
    }

}