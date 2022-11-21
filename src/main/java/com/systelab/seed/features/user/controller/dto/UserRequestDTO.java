package com.systelab.seed.features.user.controller.dto;

import com.systelab.seed.features.user.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserRequestDTO {

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(description = "Surname", example = "Barrows")
    private String surname;

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(description = "Name", example = "John")
    private String name;

    @NotNull
    @Size(min = 1, max = 10)
    @Schema(description = "Username", example = "jbarrows")
    private String login;

    @NotNull
    @Size(min = 1, max = 256)
    @Schema(description = "Password", example = "CHANGEIT")
    private String password;

    @NotNull
    @Schema(description = "Role", example = "USER")
    private UserRole role = UserRole.USER;

}
