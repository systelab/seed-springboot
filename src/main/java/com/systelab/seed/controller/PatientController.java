package com.systelab.seed.controller;

import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.repository.PatientNotFoundException;
import com.systelab.seed.repository.PatientRepository;
import com.systelab.seed.service.MedicalRecordNumberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@Api(value = "Patient", description = "API for patient management", tags = {"Patient"})
@RestController()
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization", allowCredentials = "true")
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalRecordNumberService medicalRecordNumberService;

    @ApiOperation(value = "Get all Patients", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("patients")
    @PermitAll
    public ResponseEntity<Page<Patient>> getAllPatients(Pageable pageable) {
        final PageRequest page = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "surname", "name"
        );

        return ResponseEntity.ok(patientRepository.findAll(page));
    }

    @ApiOperation(value = "Get Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("patients/{uid}")
    public ResponseEntity<Patient> getPatient(@PathVariable("uid") UUID patientId) {
        return this.patientRepository.findById(patientId).map(ResponseEntity::ok).orElseThrow(() -> new PatientNotFoundException(patientId));

    }

    @ApiOperation(value = "Create a Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @PostMapping("patients/patient")
    public ResponseEntity<Patient> createPatient(@RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient p) {
        if (p.getMedicalNumber() == null || p.getMedicalNumber().equals("")) {
            p.setMedicalNumber(medicalRecordNumberService.getMedicalRecordNumber());
        }
        Patient patient = this.patientRepository.save(p);
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}").buildAndExpand(patient.getId()).toUri();
        return ResponseEntity.created(uri).body(patient);
    }


    @ApiOperation(value = "Create or Update (idempotent) an existing Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @PutMapping("patients/{uid}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("uid") UUID patientId, @RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient p) {
        return this.patientRepository
                .findById(patientId)
                .map(existing -> {
                    p.setId(patientId);
                    Patient patient = this.patientRepository.save(p);
                    URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
                    return ResponseEntity.created(selfLink).body(patient);
                }).orElseThrow(() -> new PatientNotFoundException(patientId));
    }


    @ApiOperation(value = "Delete a Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @DeleteMapping("patients/{uid}")
    public ResponseEntity<?> removePatient(@PathVariable("uid") UUID patientId) {
        return this.patientRepository.findById(patientId)
                .map(c -> {
                    patientRepository.delete(c);
                    return ResponseEntity.noContent().build();
                }).orElseThrow(() -> new PatientNotFoundException(patientId));
    }
}