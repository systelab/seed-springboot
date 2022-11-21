package com.systelab.seed.features.user.service.command;

import com.systelab.seed.features.user.model.User;
import com.systelab.seed.features.user.repository.UserRepository;
import com.systelab.seed.features.user.service.UserNotFoundException;
import com.systelab.seed.features.user.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserDeleteCommandService {

    private final UserRepository userRepository;
    private final UserQueryService userQueryService;

    public User deleteUser(UUID id) {
        User user=userQueryService.getUser(id);
        userRepository.delete(user);
        return user;
    }
}