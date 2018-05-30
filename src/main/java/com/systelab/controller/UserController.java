package com.systelab.controller;

import com.systelab.model.user.User;
import com.systelab.model.user.UserRole;
import com.systelab.repository.PatientNotFoundException;
import com.systelab.repository.UserNotFoundException;
import com.systelab.repository.UserRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletContext;
import javax.validation.Valid;
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
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @ApiOperation(value = "User Login", notes = "")
    @PostMapping(value = "users/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @PermitAll
    public ResponseEntity authenticateUser(@RequestParam("login") String login, @RequestParam("password") String password) throws SecurityException {

        User user = userRepository.findByLoginAndPassword(login, bCryptPasswordEncoder.encode(password));

        if (login.equals("a") && password.equals("a")) {
            user = new User();
            user.setLogin("a");
            user.setPassword("a");
            user.setName("a");
            user.setSurname("a");
            user.setId(1L);
            user.setRole(UserRole.USER);
        }
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Get all Users", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @ApiOperation(value = "Create a User", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @PostMapping("users/user")
    public User createUser(@RequestBody @ApiParam(value = "User", required = true) @Valid User user) {
        user.setId(null);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @ApiOperation(value = "Get User", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("users/{uid}")
    public User getUser(@PathVariable("uid") Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent())
            throw new UserNotFoundException(userId);
        return user.get();
    }

    @ApiOperation(value = "Delete a User", notes = "", authorizations = {@Authorization(value = "Bearer")})
    @DeleteMapping("users/{uid}")
    public  ResponseEntity<?> removeUser(@PathVariable("uid") Long userId) {
        return this.userRepository.findById(userId)
                .map(c -> {
                    userRepository.delete(c);
                    return ResponseEntity.noContent().build();
                }).orElseThrow(() -> new UserNotFoundException(userId));
    }
}