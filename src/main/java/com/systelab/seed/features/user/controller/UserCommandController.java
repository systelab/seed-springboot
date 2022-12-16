package com.systelab.seed.features.user.controller;

import com.systelab.seed.features.user.controller.dto.UserMapper;
import com.systelab.seed.features.user.controller.dto.UserRequestDTO;
import com.systelab.seed.features.user.controller.dto.UserResponseDTO;
import com.systelab.seed.features.user.model.User;
import com.systelab.seed.features.user.service.command.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
@Tag(name = "User")
@RestController
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserCommandController {

    private final UserCreationCommandService userCreationCommandService;
    private final UserUpdateCommandService userUpdateCommandService;
    private final UserDeleteCommandService userDeleteCommandService;
    private final UserChangePasswordCommandService userChangePasswordCommandService;
    private final UserLoginCommandService userLoginCommandService;
    private final UserMapper userMapper;

    @Operation(description = "User Login")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)})
    @PostMapping(value = "users/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity authenticateUser(@RequestParam("login") String login, @RequestParam("password") String password) {
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + this.userLoginCommandService.authenticateUserAndGetToken(login, password)).build();
    }

    @Operation(description = "Change Password")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/password")
    public ResponseEntity<UserResponseDTO> changePassword(@RequestParam("oldpassword") String oldPassword, @RequestParam("newpassword") String newPassword, Principal principal) {
        return ResponseEntity.ok(userMapper.toResponseDTO(this.userChangePasswordCommandService.changePassword(oldPassword, newPassword, principal)));
    }

    @Operation(description = "Create a User")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("users/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Parameter(description = "User", required = true) @Valid UserRequestDTO dto) {
        User user = this.userCreationCommandService.createUser(userMapper.fromRequestDTO(dto));
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(userMapper.toResponseDTO(user));
    }

    @Operation(description = "Update a User")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("users/{uid}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable("uid") UUID id, @Valid @RequestBody @Parameter(description = "User", required = true) UserRequestDTO dto) {
        User user = this.userUpdateCommandService.updateUser(id, userMapper.fromRequestDTO(dto));
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(userMapper.toResponseDTO(user));
    }

    @Operation(description = "Delete a User")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("users/{uid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity deleteUser(@PathVariable("uid") UUID id) {
        this.userDeleteCommandService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}