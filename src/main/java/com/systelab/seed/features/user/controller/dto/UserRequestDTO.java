package com.systelab.seed.features.user.controller.dto;

import com.systelab.seed.features.user.model.UserRole;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserRequestDTO {

    @NotNull
    @Size(min = 1, max = 255)
    private String surname;

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @Size(min = 1, max = 10)
    private String login;

    @NotNull
    @Size(min = 1, max = 256)
    private String password;

    @NotNull
    private UserRole role = UserRole.USER;

}
