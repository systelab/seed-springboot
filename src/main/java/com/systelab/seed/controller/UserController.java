package com.systelab.seed.controller;

import com.systelab.seed.Constants;
import com.systelab.seed.config.authentication.TokenProvider;
import com.systelab.seed.model.user.User;
import com.systelab.seed.repository.UserNotFoundException;
import com.systelab.seed.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@Api(value = "User", description = "API for user management", tags = {"User"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization", allowCredentials = "true")
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, AuthenticationManager authenticationManager, TokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @ApiOperation(value = "User Login")
    @PostMapping(value = "users/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity authenticateUser(@RequestParam("login") String login, @RequestParam("password") String password) throws SecurityException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok().header(Constants.HEADER_STRING, "Bearer " + token).build();
    }

    @ApiOperation(value = "Get all Users", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userRepository.findAll(pageable));
    }

    @ApiOperation(value = "Get User", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("users/{uid}")
    public ResponseEntity<User> getUser(@PathVariable("uid") UUID userId) {
        return this.userRepository.findById(userId).map(ResponseEntity::ok).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @ApiOperation(value = "Create a User", authorizations = {@Authorization(value = "Bearer")})
    @PostMapping("users/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody @ApiParam(value = "User", required = true) @Valid User u) {
        u.setId(null);
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        User user = this.userRepository.save(u);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @ApiOperation(value = "Delete a User", authorizations = {@Authorization(value = "Bearer")})
    @DeleteMapping("users/{uid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> removeUser(@PathVariable("uid") UUID userId) {
        return this.userRepository.findById(userId)
                .map(u -> {
                    userRepository.delete(u);
                    return ResponseEntity.noContent().build();
                }).orElseThrow(() -> new UserNotFoundException(userId));
    }
}