package com.systelab.seed.features.patient.allergy.controller;

import com.systelab.seed.features.patient.allergy.controller.dto.PatientAllergyMapper;
import com.systelab.seed.features.patient.allergy.controller.dto.PatientAllergyRequestDTO;
import com.systelab.seed.features.patient.allergy.controller.dto.PatientAllergyResponseDTO;
import com.systelab.seed.features.patient.allergy.model.PatientAllergy;
import com.systelab.seed.features.patient.allergy.service.PatientAllergyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "PatientAllergy")
@RequiredArgsConstructor
@RestController()
@RequestMapping(value = "/seed/v1/patients", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientAllergyController {

    private final PatientAllergyService patientAllergyService;
    private final PatientAllergyMapper patientAllergyMapper;

    @Operation(description = "Get Allergies from Patient")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("{uid}/allergies")
    public ResponseEntity<Set<PatientAllergyResponseDTO>> getPatientAllergies(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.patientAllergyService.getAllergiesFromPatient(id).stream().map(patientAllergyMapper::toResponseDTO).collect(Collectors.toSet()));
    }

    @Operation(description = "Update an allergy to a Patient")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("{patientUid}/allergies/{allergyUid}")
    public ResponseEntity<PatientAllergyResponseDTO> updatePatientAllergy(@PathVariable("patientUid") UUID patientId, @PathVariable("allergyUid") UUID allergyId,
                                                               @RequestBody @Parameter(description = "patientAllergy", required = true) @Valid PatientAllergyRequestDTO dto) {
        PatientAllergy allergies = this.patientAllergyService.updateAllergyToPatient(patientId, allergyId, patientAllergyMapper.fromRequestDTO(dto));
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(patientAllergyMapper.toResponseDTO(allergies));
    }

    @Operation(description = "Add an Allergy to a Patient")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("{uid}/allergies/allergy")
    public ResponseEntity<PatientAllergyResponseDTO> addPatientAllergy(@PathVariable("uid") UUID id,
                                                            @RequestBody @Parameter(description = "allergy", required = true) @Valid PatientAllergyRequestDTO dto) {
        PatientAllergy allergies = this.patientAllergyService.addAllergyToPatient(id, patientAllergyMapper.fromRequestDTO(dto));
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/patients/{id}").buildAndExpand(allergies.getId()).toUri();
        return ResponseEntity.created(uri).body(patientAllergyMapper.toResponseDTO(allergies));
    }

    @Operation(description = "Delete an Allergy from a Patient")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("{patientUid}/allergies/{allergyUid}")
    public ResponseEntity<PatientAllergyResponseDTO> removePatientAllergy(@PathVariable("patientUid") UUID patientId, @PathVariable("allergyUid") UUID allergyId) {
        this.patientAllergyService.removeAllergyFromPatient(patientId, allergyId);
        return ResponseEntity.noContent().build();
    }

}
