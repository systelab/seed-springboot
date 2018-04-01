package com.systelab.controller;

import com.systelab.model.patient.Patient;
import com.systelab.repository.PatientNotFoundException;
import com.systelab.repository.PatientRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @ApiOperation(value = "Get all Patients", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "An array of Patient", response = Patient.class, responseContainer = "List"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @PermitAll
    @GetMapping("patients")
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @ApiOperation(value = "Create a Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "A Patient", response = Patient.class), @ApiResponse(code = 400, message = "Validation exception"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @PostMapping("patients/patient")
    @PermitAll
    public Patient createPatient(@RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient patient) {
        patient.setId(null);
        return patientRepository.save(patient);
    }

    @ApiOperation(value = "Create or Update (idempotent) an existing Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "A Patient", response = Patient.class), @ApiResponse(code = 400, message = "Validation exception"), @ApiResponse(code = 404, message = "Patient not found"),
                    @ApiResponse(code = 500, message = "Internal Server Error")})
    @PutMapping("patients/{uid}")
    @PermitAll
    public Patient updatePatient(@PathVariable("uid") Long patientId, @RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient patient) {
        Optional<Patient> patientOptional = patientRepository.findById(patientId);
        if (!patientOptional.isPresent())
            throw new PatientNotFoundException("id-" + patientId);

        patient.setId(patientId);
        return patientRepository.save(patient);
    }

    @ApiOperation(value = "Get Patient", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "A Patient", response = Patient.class), @ApiResponse(code = 404, message = "Patient not found"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @GetMapping("patients/{uid}")
    @PermitAll
    public Patient getPatient(@PathVariable("uid") Long patientId) {
        Optional<Patient> patient = patientRepository.findById(patientId);
        if (!patient.isPresent())
            throw new PatientNotFoundException("id-" + patientId);
        return patient.get();
    }

    @ApiOperation(value = "Delete a Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @DeleteMapping("patients/{uid}")
    @RolesAllowed("ADMIN")
    public void removePatient(@PathVariable("uid") Long patientId) {
        patientRepository.deleteById(patientId);
    }
}