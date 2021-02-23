package com.systelab.seed.patient.allergy.controller;

import com.systelab.seed.patient.allergy.model.PatientAllergy;
import com.systelab.seed.patient.allergy.service.PatientAllergyService;
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

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Tag(name = "PatientAllergy")
@RestController()
@RequestMapping(value = "/seed/v1/patients", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientAllergyController {

    private final PatientAllergyService patientAllergyService;

    @Autowired
    public PatientAllergyController(PatientAllergyService patientAllergyService) {
        this.patientAllergyService = patientAllergyService;
    }

    @Operation(description = "Get Allergies from Patient")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("{uid}/allergies")
    public ResponseEntity<Set<PatientAllergy>> getPatientAllergies(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.patientAllergyService.getAllergiesFromPatient(id));
    }

    @Operation(description = "Update an allergy to a Patient")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("{patientUid}/allergies/{allergyUid}")
    public ResponseEntity<PatientAllergy> updatePatientAllergy(@PathVariable("patientUid") UUID patientId, @PathVariable("allergyUid") UUID allergyId,
                                                               @RequestBody @Parameter(description = "patientAllergy", required = true) @Valid PatientAllergy patientAllergy) {
        PatientAllergy patient = this.patientAllergyService.updateAllergyToPatient(patientId, allergyId, patientAllergy);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(patient);
    }

    @Operation(description = "Add an Allergy to a Patient")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("{uid}/allergies/allergy")
    public ResponseEntity<PatientAllergy> addPatientAllergy(@PathVariable("uid") UUID id,
                                                            @RequestBody @Parameter(description = "allergy", required = true) @Valid PatientAllergy pa) {
        PatientAllergy patient = this.patientAllergyService.addAllergyToPatient(id, pa);
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/patients/{id}").buildAndExpand(patient.getId()).toUri();
        return ResponseEntity.created(uri).body(patient);
    }

    @Operation(description = "Delete an Allergy from a Patient")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("{patientUid}/allergies/{allergyUid}")
    public ResponseEntity<PatientAllergy> removePatientAllergy(@PathVariable("patientUid") UUID patientId, @PathVariable("allergyUid") UUID allergyId) {
        this.patientAllergyService.removeAllergyFromPatient(patientId, allergyId);
        return ResponseEntity.noContent().build();
    }

}
