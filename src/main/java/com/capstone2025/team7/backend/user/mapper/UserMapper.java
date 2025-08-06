package com.capstone2025.team7.backend.user.mapper;

import com.capstone2025.team7.backend.user.dto.UserDto;
import com.capstone2025.team7.backend.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userPostToUser(UserDto.Post post);

    User userPatchToUser(UserDto.Patch patch);

    UserDto.Response userToUserResponse(User user);
}
