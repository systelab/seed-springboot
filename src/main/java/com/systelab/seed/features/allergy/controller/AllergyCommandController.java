package com.systelab.seed.features.allergy.controller;

import com.systelab.seed.features.allergy.controller.dto.AllergyMapper;
import com.systelab.seed.features.allergy.controller.dto.AllergyRequestDTO;
import com.systelab.seed.features.allergy.controller.dto.AllergyResponseDTO;
import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.allergy.service.command.AllergyCreationCommandService;
import com.systelab.seed.features.allergy.service.command.AllergyDeleteCommandService;
import com.systelab.seed.features.allergy.service.command.AllergyUpdateCommandService;
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
import java.util.UUID;

@Tag(name = "Allergy")
@RequiredArgsConstructor
@RestController()
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class AllergyCommandController {

    private final AllergyCreationCommandService allergyCreationCommandService;
    private final AllergyUpdateCommandService allergyUpdateCommandService;
    private final AllergyDeleteCommandService allergyDeleteCommandService;
    private final AllergyMapper allergyMapper;

    @Operation(description = "Create an Allergy")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("allergies/allergy")
    public ResponseEntity<AllergyResponseDTO> createAllergy(@RequestBody @Parameter(description = "Allergy", required = true) @Valid AllergyRequestDTO dto) {
        Allergy createdAllergy = this.allergyCreationCommandService.createAllergy(allergyMapper.fromRequestDTO(dto));
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/allergies/{id}").buildAndExpand(createdAllergy.getId()).toUri();
        return ResponseEntity.created(uri).body(allergyMapper.toResponseDTO(createdAllergy));
    }

    @Operation(description = "Create or Update (idempotent) an existing Allergy")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("allergies/{uid}")
    public ResponseEntity<AllergyResponseDTO> updateAllergy(@PathVariable("uid") UUID id, @RequestBody @Parameter(description = "Allergy", required = true) @Valid AllergyRequestDTO dto) {
        Allergy updatedAllergy = this.allergyUpdateCommandService.updateAllergy(id, allergyMapper.fromRequestDTO(dto));
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(allergyMapper.toResponseDTO(updatedAllergy));
    }

    @Operation(description = "Delete an Allergy")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("allergies/{uid}")
    public ResponseEntity removeAllergy(@PathVariable("uid") UUID id) {
        this.allergyDeleteCommandService.removeAllergy(id);
        return ResponseEntity.noContent().build();
    }
}