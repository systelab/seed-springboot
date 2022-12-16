package com.systelab.seed.features.user.service.command;

import com.systelab.seed.features.user.model.User;
import com.systelab.seed.features.user.repository.UserRepository;
import com.systelab.seed.features.user.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserUpdateCommandService {

    private final UserRepository userRepository;
    private final UserQueryService userQueryService;

    public User updateUser(UUID id, User u) {
        User user=userQueryService.getUser(id);
        u.setId(user.getId());
        u.setPassword(user.getPassword());
        return this.userRepository.save(u);
    }

}