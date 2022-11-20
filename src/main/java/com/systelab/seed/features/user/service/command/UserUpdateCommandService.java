package com.systelab.seed.features.user.service.command;

import com.systelab.seed.features.user.model.User;
import com.systelab.seed.features.user.repository.UserRepository;
import com.systelab.seed.features.user.service.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserUpdateCommandService {

    private final UserRepository userRepository;

    public User updateUser(UUID id, User u) {
        return this.userRepository.findById(id)
                .map(existing -> {
                    u.setId(id);
                    u.setPassword(existing.getPassword());
                    return this.userRepository.save(u);
                }).orElseThrow(() -> new UserNotFoundException(id));
    }

}