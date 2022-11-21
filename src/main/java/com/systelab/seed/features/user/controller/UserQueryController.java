package com.systelab.seed.features.user.controller;

import com.systelab.seed.features.user.controller.dto.UserMapper;
import com.systelab.seed.features.user.controller.dto.UserResponseDTO;
import com.systelab.seed.features.user.service.query.UserQueryService;
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
import java.util.UUID;

@RequiredArgsConstructor
@Tag(name = "User")
@RestController
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserQueryController {

    private final UserQueryService userQueryService;
    private final UserMapper userMapper;


    @Operation(description = "Get all Users")
    @SecurityRequirement(name = "Authorization")
    @PageableAsQueryParam
    @GetMapping("users")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(this.userQueryService.getAllUsers(pageable).map(userMapper::toResponseDTO));
    }

    @Operation(description = "Get User")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("users/{uid}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(userMapper.toResponseDTO(this.userQueryService.getUser(id)));
    }
}