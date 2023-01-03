package com.systelab.seed.features.user.controller.dto;

import com.systelab.seed.features.user.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    User fromRequestDTO(UserRequestDTO dto);

    UserResponseDTO toResponseDTO(User user);
}
