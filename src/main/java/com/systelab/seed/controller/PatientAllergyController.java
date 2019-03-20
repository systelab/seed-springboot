package com.systelab.seed.controller;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.systelab.seed.model.patient.PatientAllergy;
import com.systelab.seed.service.PatientAllergyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@Api(value = "Patient Allergy", description = "API for Patient Allergy management", tags = { "PatientAllergy" })
@RestController()
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization", allowCredentials = "true")
@RequestMapping(value = "/seed/v1/patients", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientAllergyController {

    private final PatientAllergyService patientAllergyService;

    @Autowired
    public PatientAllergyController(PatientAllergyService patientAllergyService) {
        this.patientAllergyService = patientAllergyService;
    }

    @ApiOperation(value = "Get Allergies from Patient", authorizations = { @Authorization(value = "Bearer") })
    @GetMapping("{uid}/allergies")
    public ResponseEntity<Set<PatientAllergy>> getPatientAllergies(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.patientAllergyService.getAllergiesFromPatient(id));
    }

    @ApiOperation(value = "Update an alergy to a Patient", authorizations = { @Authorization(value = "Bearer") })
    @PutMapping("{patientUid}/allergies/{allergyUid}")
    public ResponseEntity<PatientAllergy> updatePatientAllergy(@PathVariable("patientUid") UUID patientId, @PathVariable("allergyUid") UUID allergyId,
            @RequestBody @ApiParam(value = "patientAllergy", required = true) @Valid PatientAllergy patientAllergy) {
        PatientAllergy patient = this.patientAllergyService.updateAlergyToPatient(patientId, allergyId, patientAllergy);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(patient);
    }

    @ApiOperation(value = "Add an Allergy to a Patient", authorizations = { @Authorization(value = "Bearer") })
    @PostMapping("{uid}/allergies/allergy")
    public ResponseEntity<PatientAllergy> addAllergyAddToPatient(@PathVariable("uid") UUID id,
            @RequestBody @ApiParam(value = "allergy", required = true) @Valid PatientAllergy pa) {
        PatientAllergy patient = this.patientAllergyService.createAllergyToPatient(id, pa);
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/patients/{id}").buildAndExpand(patient.getId()).toUri();
        return ResponseEntity.created(uri).body(patient);
    }

    @ApiOperation(value = "Delete an Allergy from a Patient", authorizations = { @Authorization(value = "Bearer") })
    @DeleteMapping("{patientUid}/allergies/{allergyUid}")
    public ResponseEntity removeAllergyFromPatient(@PathVariable("patientUid") UUID patientId, @PathVariable("allergyUid") UUID allergyId) {
        this.patientAllergyService.removeAlleryFromPatient(patientId, allergyId);
        return ResponseEntity.noContent().build();
    }

}
