package com.systelab.controller;

import com.systelab.model.patient.Patient;
import com.systelab.model.user.UserRole;
import com.systelab.repository.PatientNotFoundException;
import com.systelab.repository.PatientRepository;
import com.systelab.repository.UserNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Api(value = "Patient", description = "API for patient management", tags = {"Patient"})
@RestController()
@CrossOrigin()
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @ApiOperation(value = "Get all Patients", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("patients")
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @ApiOperation(value = "Create a Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @PostMapping("patients/patient")
    public Patient createPatient(@RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient patient) {
        patient.setId(null);
        return patientRepository.save(patient);
    }

    @ApiOperation(value = "Create or Update (idempotent) an existing Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @PutMapping("patients/{uid}")
    public Patient updatePatient(@PathVariable("uid") Long patientId, @RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient patient) {
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        if (!patientOptional.isPresent())
            throw new PatientNotFoundException(patientId);

        patient.setId(patientId);
        return patientRepository.save(patient);
    }

    @ApiOperation(value = "Get Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("patients/{uid}")
    public Patient getPatient(@PathVariable("uid") Long patientId) {
        Optional<Patient> patient = patientRepository.findById(patientId);
        if (!patient.isPresent())
            throw new PatientNotFoundException(patientId);
        return patient.get();
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