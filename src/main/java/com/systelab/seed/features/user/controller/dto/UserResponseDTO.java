package com.systelab.seed.features.user.controller.dto;

import com.systelab.seed.features.user.model.UserRole;
import lombok.Data;

import java.util.UUID;

@Data
public class UserResponseDTO {

    private UUID id;

    private String surname;

    private String name;

    private String login;

    private UserRole role = UserRole.USER;

}
