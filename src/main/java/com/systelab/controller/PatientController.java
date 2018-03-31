package com.systelab.controller;

import com.systelab.model.Patient;
import com.systelab.service.PatientService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@Api(value = "Patient")
@RestController
@RequestMapping("/seed/v1")
public class PatientController {

    public static PatientService patientService = new PatientService();


    @ApiOperation(value = "Get all Patients", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "An array of Patient", response = Patient.class, responseContainer = "List"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @PermitAll
    @GetMapping("patients")
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    @ApiOperation(value = "Create a Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "A Patient", response = Patient.class), @ApiResponse(code = 400, message = "Validation exception"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @PostMapping("patients/patient")
    @PermitAll
    public Patient createPatient(@RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient patient) {
        patient.setId(null);
        patientService.create(patient);
        return patient;
    }

    @ApiOperation(value = "Create or Update (idempotent) an existing Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(
            value = {@ApiResponse(code = 200, message = "A Patient", response = Patient.class), @ApiResponse(code = 400, message = "Validation exception"), @ApiResponse(code = 404, message = "Patient not found"),
                    @ApiResponse(code = 500, message = "Internal Server Error")})
    @PutMapping("patients/{uid}")
    @PermitAll
    public Patient updatePatient(@PathVariable("uid") Long patientId, @RequestBody @ApiParam(value = "Patient", required = true) @Valid Patient patient) {
        patient.setId(patientId);
        Patient updatedPatient = patientService.update(patientId, patient);
        return updatedPatient;
    }

    @ApiOperation(value = "Get Patient", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "A Patient", response = Patient.class), @ApiResponse(code = 404, message = "Patient not found"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @GetMapping("patients/{uid}")
    @PermitAll
    public Patient getPatient(@PathVariable("uid") Long patientId) {
        return patientService.getPatient(patientId);
    }

    @ApiOperation(value = "Delete a Patient", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @DeleteMapping("patients/{uid}")
    @RolesAllowed("ADMIN")
    public void remove(@PathVariable("uid") Long patientId) {
        patientService.delete(patientId);
    }
}