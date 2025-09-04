// UserClubMapper.java
package com.capstone2025.team7.backend.userClub.mapper;

import com.capstone2025.team7.backend.userClub.dto.UserClubDto;
import com.capstone2025.team7.backend.userClub.entity.UserClub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserClubMapper {

    // Post DTO -> Entity
    @Mapping(target = "userClubId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "club", ignore = true)
    @Mapping(target = "userClubStatuses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    UserClub postDtoToEntity(UserClubDto.Post postDto);

    // Entity -> Response DTO
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "nickname", source = "user.nickname")
    @Mapping(target = "clubName", source = "club.clubName")
    @Mapping(target = "age", source = "user.age")
    @Mapping(target = "userClubStatusList", source = "userClubStatuses")
    UserClubDto.Response entityToResponseDto(UserClub userClub);

    // List mappings
    List<UserClubDto.Response> entitiesToResponseDtos(List<UserClub> userClubs);
}