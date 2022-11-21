package com.systelab.seed.features.user.service.command;

import com.systelab.seed.features.user.model.User;
import com.systelab.seed.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCreationCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User createUser(User u) {
        u.setId(null);
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return this.userRepository.save(u);
    }
}