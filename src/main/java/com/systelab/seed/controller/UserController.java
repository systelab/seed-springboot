package com.systelab.seed.controller;

import com.systelab.seed.Constants;
import com.systelab.seed.config.TokenProvider;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.net.URI;

@Api(value = "User", description = "API for user management", tags = {"User"})
@RestController
@CrossOrigin()
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @ApiOperation(value = "User Login", notes = "")
    @PostMapping(value = "users/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @PermitAll
    public ResponseEntity authenticateUser(@RequestParam("login") String login, @RequestParam("password") String password) throws SecurityException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        return ResponseEntity.ok().header(Constants.HEADER_STRING, "Bearer " + token).build();
    }

    @ApiOperation(value = "Get all Users", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userRepository.findAll(pageable));
    }

    @ApiOperation(value = "Get User", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("users/{uid}")
    public ResponseEntity<User> getUser(@PathVariable("uid") Long userId) {
        return this.userRepository.findById(userId).map(ResponseEntity::ok).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @ApiOperation(value = "Create a User", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @PostMapping("users/user")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody @ApiParam(value = "User", required = true) @Valid User u) {
        u.setId(null);
        u.setPassword(bCryptPasswordEncoder.encode(u.getPassword()));
        User user = this.userRepository.save(u);

        URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @ApiOperation(value = "Delete a User", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @DeleteMapping("users/{uid}")
    public ResponseEntity<?> removeUser(@PathVariable("uid") Long userId) {
        return this.userRepository.findById(userId)
                .map(c -> {
                    userRepository.delete(c);
                    return ResponseEntity.noContent().build();
                }).orElseThrow(() -> new UserNotFoundException(userId));
    }
}