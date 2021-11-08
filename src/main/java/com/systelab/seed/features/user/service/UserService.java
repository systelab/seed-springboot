package com.systelab.seed.features.user.service;

import com.systelab.seed.core.security.config.TokenProvider;
import com.systelab.seed.features.user.model.User;
import com.systelab.seed.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public String authenticateUserAndGetToken(String login, String password) {

        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.generateToken(authentication);
    }

    public User changePassword(String oldPassword, String newPassword, Principal principal) {

        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(principal.getName(), oldPassword));

        if (authentication.isAuthenticated()) {
            return this.userRepository.findByLogin(principal.getName()).map(existing -> {
                existing.setPassword(passwordEncoder.encode(newPassword));
                return this.userRepository.save(existing);
            }).orElseThrow(SecurityException::new);
        }
        throw new SecurityException();
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUser(UUID id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User createUser(User u) {
        u.setId(null);
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return this.userRepository.save(u);
    }

    public User updateUser(UUID id, User u) {
        return this.userRepository.findById(id)
                .map(existing -> {
                    u.setId(id);
                    u.setPassword(existing.getPassword());
                    return this.userRepository.save(u);
                }).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User deleteUser(UUID id) {
        return this.userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return user;
                }).orElseThrow(() -> new UserNotFoundException(id));
    }
}