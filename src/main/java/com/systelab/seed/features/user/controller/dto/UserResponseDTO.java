package com.systelab.seed.features.user.controller.dto;

import com.systelab.seed.features.user.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class UserResponseDTO {

    @Schema(description = "Internal ID", example = "30649644-80ce-42eb-816f-7b730b9eddce")
    private UUID id;

    @Schema(description = "Surname", example = "Barrows")
    private String surname;

    @Schema(description = "Name", example = "John")
    private String name;

    @Schema(description = "Username", example = "jbarrows")
    private String login;

    @Schema(description = "Role", example = "USER")
    private UserRole role = UserRole.USER;

}
