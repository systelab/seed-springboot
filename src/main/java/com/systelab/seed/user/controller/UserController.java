package com.systelab.seed.user.controller;

import com.systelab.seed.user.model.User;
import com.systelab.seed.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
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

@Api(value = "User", description = "API for user management", tags = {"User"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization", allowCredentials = "true")
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "User Login")
    @PostMapping(value = "users/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity authenticateUser(@RequestParam("login") String login, @RequestParam("password") String password) {
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + this.userService.authenticateUserAndGetToken(login, password)).build();
    }

    @ApiOperation(value = "Change Password", authorizations = {@Authorization(value = "Bearer")})
    @PostMapping("/password")
    public ResponseEntity<User> changePassword(@RequestParam("oldpassword") String oldPassword, @RequestParam("newpassword") String newPassword, Principal principal) {
        return ResponseEntity.ok(this.userService.changePassword(oldPassword, newPassword, principal));
    }

    @ApiOperation(value = "Get all Users", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(this.userService.getAllUsers(pageable));
    }

    @ApiOperation(value = "Get User", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("users/{uid}")
    public ResponseEntity<User> getUser(@PathVariable("uid") UUID id) {
        return ResponseEntity.ok(this.userService.getUser(id));
    }

    @ApiOperation(value = "Create a User", authorizations = {@Authorization(value = "Bearer")})
    @PostMapping("users/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody @ApiParam(value = "User", required = true) @Valid User u) {
        User user = this.userService.createUser(u);
        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @ApiOperation(value = "Update a User", authorizations = {@Authorization(value = "Bearer")})
    @PutMapping("users/{uid}")
    public ResponseEntity<User> updateUser(@PathVariable("uid") UUID id, @Valid @RequestBody @ApiParam(value = "User", required = true) User u) {
        User user = this.userService.updateUser(id, u);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(user);
    }

    @ApiOperation(value = "Delete a User", authorizations = {@Authorization(value = "Bearer")})
    @DeleteMapping("users/{uid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity deleteUser(@PathVariable("uid") UUID id) {
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}