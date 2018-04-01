package com.systelab.controller;

import com.systelab.infraestructure.JWTAuthenticationTokenGenerator;
import com.systelab.model.user.User;
import com.systelab.repository.PatientNotFoundException;
import com.systelab.repository.UserRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Api(value = "User", description = "API for user management", tags = {"User"})
@RestController
@CrossOrigin()
@RequestMapping(value = "/seed/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ServletContext servletContext;

    @Autowired
    private JWTAuthenticationTokenGenerator tokenGenerator;

    @ApiOperation(value = "User Login", notes = "")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "An authorization key in the header"), @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @PostMapping(value = "users/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @PermitAll
    public ResponseEntity authenticateUser(@RequestParam("login") String login, @RequestParam("password") String password) throws SecurityException {

        final User user = userRepository.findByLoginAndPassword(login, password);
        if (user!=null) {
            final Instant now = Instant.now();

            final String jwt = tokenGenerator.issueToken(login, user.getRole().name(), servletContext.getContextPath().toString());
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "origin, content-type, accept, authorization, ETag, if-none-match")
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "origin, content-type, accept, authorization, ETag, if-none-match")
                    .build();
        }
        else {
            throw new SecurityException();
        }
    }

    @ApiOperation(value = "Get all Users", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "An array of Users", response = User.class, responseContainer = "List"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @GetMapping("users")
    @RolesAllowed("ADMIN")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @ApiOperation(value = "Create a User", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "A User", response = User.class), @ApiResponse(code = 400, message = "Validation exception"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @PostMapping("users/user")
    @RolesAllowed("ADMIN")
    public User createUser(@RequestBody @ApiParam(value = "User", required = true) @Valid User user) {
        user.setId(null);
        return userRepository.save(user);
    }

    @ApiOperation(value = "Get User", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "A user", response = User.class), @ApiResponse(code = 404, message = "User not found"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @GetMapping("users/{uid}")
    @PermitAll
    public User getUser(@PathVariable("uid") Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent())
            throw new PatientNotFoundException("id-" + userId);
        return user.get();
    }

    @ApiOperation(value = "Delete a User", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 500, message = "Internal Server Error")})
    @DeleteMapping("users/{uid}")
    @RolesAllowed("ADMIN")
    public void removeUser(@PathVariable("uid") Long userId) {
        userRepository.deleteById(userId);
    }
}