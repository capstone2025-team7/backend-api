package com.capstone2025.team7.backend.userVote.mapper;

import com.capstone2025.team7.backend.userVote.dto.UserVoteDto;
import com.capstone2025.team7.backend.userVote.entity.UserVote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserVoteMapper {

    // Post DTO -> Entity
    @Mapping(target = "userVoteId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "vote", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    UserVote postDtoToEntity(UserVoteDto.Post postDto);

    // Entity -> Response DTO
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "voteId", source = "vote.voteId")
    @Mapping(target = "voteTitle", source = "vote.title")
    @Mapping(target = "voteDescription", source = "vote.description")
    UserVoteDto.Response entityToResponseDto(UserVote userVote);


    // Helper method for checking if vote is active
    @Named("isVoteActive")
    default Boolean isVoteActive(com.capstone2025.team7.backend.vote.entity.Vote vote) {
        if (vote == null || vote.getDueDate() == null) {
            return false;
        }
        return vote.getDueDate().isAfter(LocalDateTime.now());
    }

    // List mappings
    List<UserVoteDto.Response> entitiesToResponseDtos(List<UserVote> userVotes);
}