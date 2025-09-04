package com.capstone2025.team7.backend.vote.mapper;

import com.capstone2025.team7.backend.vote.dto.VoteDto;
import com.capstone2025.team7.backend.vote.entity.Vote;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VoteMapper {

    @Mapping(target = "voteId", ignore = true)
    @Mapping(target = "club", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "voteStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(source = "description", target = "description") // DTO의 description -> Entity의 descriptions
    Vote postDtoToVote(VoteDto.Post postDto);

    @Mapping(source = "club.clubId", target = "clubId")
    @Mapping(source = "club.clubName", target = "clubName")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "description", target = "description") // Entity의 descriptions -> DTO의 description
    VoteDto.Response voteToResponseDto(Vote vote);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "voteId", ignore = true)
    @Mapping(target = "club", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "voteStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(source = "description", target = "description") // DTO의 description -> Entity의 descriptions
    void updateVoteFromPatchDto(VoteDto.Patch patchDto, @MappingTarget Vote vote);
}