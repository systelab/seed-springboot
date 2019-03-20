package com.systelab.seed.controller;

import java.net.URI;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.systelab.seed.model.patient.Patient;
import com.systelab.seed.service.PatientService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@Api(value = "Patient", description = "API for patient management", tags = { "Patient" })
@RestController()
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization", allowCredentials = "true")
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @ApiOperation(value = "Get all Patients", authorizations = { @Authorization(value = "Bearer") })
    @GetMapping("patients")
    public ResponseEntity<Page<Patient>> getAllPatients(Pageable pageable) {
        return ResponseEntity.ok(this.patientService.getAllPatients(pageable));
    }

    @ApiOperation(value = "Get Patient", authorizations = { @Authorization(value = "Bearer") })
    @GetMapping("patients/{uid}")
    public ResponseEntity<Patient> getPatient(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.patientService.getPatient(id));
    }

    @ApiOperation(value = "Create a Patient", authorizations = { @Authorization(value = "Bearer") })
    @PostMapping("patients/patient")
    public ResponseEntity<Patient> createPatient(@RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient p) {
        Patient patient = this.patientService.createPatient(p);
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/patients/{id}").buildAndExpand(patient.getId()).toUri();
        return ResponseEntity.created(uri).body(patient);
    }

    @ApiOperation(value = "Create or Update (idempotent) an existing Patient", authorizations = { @Authorization(value = "Bearer") })
    @PutMapping("patients/{uid}")
    public ResponseEntity<Patient> updatePatient(@PathVariable("uid") UUID id, @RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient p) {
        Patient patient = this.patientService.updatePatient(id, p);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(patient);
    }

    @ApiOperation(value = "Delete a Patient", authorizations = { @Authorization(value = "Bearer") })
    @DeleteMapping("patients/{uid}")
    public ResponseEntity removePatient(@PathVariable("uid") UUID id) {
        this.patientService.removePatient(id);
        return ResponseEntity.noContent().build();
    }

}