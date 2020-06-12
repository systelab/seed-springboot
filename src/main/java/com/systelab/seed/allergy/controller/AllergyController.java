package com.systelab.seed.allergy.controller;

import com.systelab.seed.allergy.model.Allergy;
import com.systelab.seed.allergy.service.AllergyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.data.rest.converters.PageableAsQueryParam;
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
import java.util.UUID;

@Tag(name = "Allergy")
@RestController()
// Bad idea to have that in production
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization", allowCredentials = "true")
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class AllergyController {

    private final AllergyService allergyService;

    @Autowired
    public AllergyController(AllergyService allergyService) {
        this.allergyService = allergyService;
    }

    @Operation(description = "Get all Allergies")
    @PageableAsQueryParam
    @SecurityRequirement(name = "Authorization")
    @GetMapping("allergies")
    public ResponseEntity<Page<Allergy>> getAllAllergies(@Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(this.allergyService.getAllAllergies(pageable));
    }

    @Operation(description = "Get an Allergy")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("allergies/{uid}")
    public ResponseEntity<Allergy> getAllergy(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.allergyService.getAllergy(id));
    }

    @Operation(description = "Create an Allergy")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("allergies/allergy")
    public ResponseEntity<Allergy> createAllergy(@RequestBody @Parameter(description = "Allergy", required = true) @Valid Allergy allergy) {
        Allergy createdAllergy = this.allergyService.createAllergy(allergy);
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/allergies/{id}").buildAndExpand(createdAllergy.getId()).toUri();
        return ResponseEntity.created(uri).body(createdAllergy);
    }

    @Operation(description = "Create or Update (idempotent) an existing Allergy")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("allergies/{uid}")
    public ResponseEntity<Allergy> updateAllergy(@PathVariable("uid") UUID id, @RequestBody @Parameter(description = "Allergy", required = true) @Valid Allergy allergy) {
        Allergy updatedAllergy = this.allergyService.updateAllergy(id, allergy);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(updatedAllergy);
    }

    @Operation(description = "Delete an Allergy")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("allergies/{uid}")
    public ResponseEntity removeAllergy(@PathVariable("uid") UUID id) {
        this.allergyService.removeAllergy(id);
        return ResponseEntity.noContent().build();
    }
}