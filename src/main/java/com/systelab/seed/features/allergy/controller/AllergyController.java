package com.systelab.seed.features.allergy.controller;

import com.systelab.seed.features.allergy.controller.dto.AllergyMapper;
import com.systelab.seed.features.allergy.controller.dto.AllergyRequestDTO;
import com.systelab.seed.features.allergy.controller.dto.AllergyResponseDTO;
import com.systelab.seed.features.allergy.model.Allergy;
import com.systelab.seed.features.allergy.service.AllergyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class AllergyController {

    private final AllergyService allergyService;
    private final AllergyMapper allergyMapper;

    @Operation(description = "Get all Allergies")
    @PageableAsQueryParam
    @SecurityRequirement(name = "Authorization")
    @GetMapping("allergies")
    public ResponseEntity<Page<AllergyResponseDTO>> getAllAllergies(@Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(this.allergyService.getAllAllergies(pageable).map(allergyMapper::toResponseDTO));
    }

    @Operation(description = "Get an Allergy")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("allergies/{uid}")
    public ResponseEntity<AllergyResponseDTO> getAllergy(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(allergyMapper.toResponseDTO(this.allergyService.getAllergy(id)));
    }

    @Operation(description = "Create an Allergy")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("allergies/allergy")
    public ResponseEntity<AllergyResponseDTO> createAllergy(@RequestBody @Parameter(description = "Allergy", required = true) @Valid AllergyRequestDTO dto) {
        Allergy createdAllergy = this.allergyService.createAllergy(allergyMapper.fromRequestDTO(dto));
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/allergies/{id}").buildAndExpand(createdAllergy.getId()).toUri();
        return ResponseEntity.created(uri).body(allergyMapper.toResponseDTO(createdAllergy));
    }

    @Operation(description = "Create or Update (idempotent) an existing Allergy")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("allergies/{uid}")
    public ResponseEntity<AllergyResponseDTO> updateAllergy(@PathVariable("uid") UUID id, @RequestBody @Parameter(description = "Allergy", required = true) @Valid AllergyRequestDTO dto) {
        Allergy updatedAllergy = this.allergyService.updateAllergy(id, allergyMapper.fromRequestDTO(dto));
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(allergyMapper.toResponseDTO(updatedAllergy));
    }

    @Operation(description = "Delete an Allergy")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("allergies/{uid}")
    public ResponseEntity removeAllergy(@PathVariable("uid") UUID id) {
        this.allergyService.removeAllergy(id);
        return ResponseEntity.noContent().build();
    }
}