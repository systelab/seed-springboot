package com.systelab.seed.controller;

import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.repository.PatientNotFoundException;
import com.systelab.seed.repository.PatientRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
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

@Api(value = "Patient", description = "API for patient management", tags = {"Patient"})
@RestController()
@CrossOrigin()
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @ApiOperation(value = "Get all Patients", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("patients")
    public ResponseEntity<Page<Patient>> getAllPatients(Pageable pageable) {
        return ResponseEntity.ok(patientRepository.findAll(pageable));
    }

    @ApiOperation(value = "Get Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("patients/{uid}")
    public ResponseEntity<Patient> getPatient(@PathVariable("uid") Long patientId) {
        return this.patientRepository.findById(patientId).map(ResponseEntity::ok).orElseThrow(() -> new PatientNotFoundException(patientId));

    }

    @ApiOperation(value = "Create a Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @PostMapping("patients/patient")
    public ResponseEntity<Patient> createPatient(@RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient p) {
        Patient patient = this.patientRepository.save(p);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}").buildAndExpand(patient.getId()).toUri();
        return ResponseEntity.created(uri).body(patient);
    }

    @ApiOperation(value = "Create or Update (idempotent) an existing Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @PutMapping("patients/{uid}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("uid") Long patientId, @RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient p) {
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
    public ResponseEntity<?> removePatient(@PathVariable("uid") Long patientId) {
        return this.patientRepository.findById(patientId)
                .map(c -> {
                    patientRepository.delete(c);
                    return ResponseEntity.noContent().build();
                }).orElseThrow(() -> new PatientNotFoundException(patientId));
    }
}