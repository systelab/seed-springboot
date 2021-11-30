package com.systelab.seed.features.user.controller;

import com.systelab.seed.features.user.model.User;
import com.systelab.seed.features.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
@Tag(name = "User")
@RestController
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    @Operation(description = "User Login")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(mediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)})
    @PostMapping(value = "users/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity authenticateUser(@RequestParam("login") String login, @RequestParam("password") String password) {
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + this.userService.authenticateUserAndGetToken(login, password)).build();
    }

    @Operation(description = "Change Password")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/password")
    public ResponseEntity<User> changePassword(@RequestParam("oldpassword") String oldPassword, @RequestParam("newpassword") String newPassword, Principal principal) {
        return ResponseEntity.ok(this.userService.changePassword(oldPassword, newPassword, principal));
    }

    @Operation(description = "Get all Users")
    @SecurityRequirement(name = "Authorization")
    @PageableAsQueryParam
    @GetMapping("users")
    public ResponseEntity<Page<User>> getAllUsers(@Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(this.userService.getAllUsers(pageable));
    }

    @Operation(description = "Get User")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("users/{uid}")
    public ResponseEntity<User> getUser(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.userService.getUser(id));
    }

    @Operation(description = "Create a User")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("users/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody @Parameter(description = "User", required = true) @Valid User u) {
        User user = this.userService.createUser(u);
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @Operation(description = "Update a User")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("users/{uid}")
    public ResponseEntity<User> updateUser(@PathVariable("uid") UUID id, @Valid @RequestBody @Parameter(description = "User", required = true) User u) {
        User user = this.userService.updateUser(id, u);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(user);
    }

    @Operation(description = "Delete a User")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("users/{uid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity deleteUser(@PathVariable("uid") UUID id) {
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}