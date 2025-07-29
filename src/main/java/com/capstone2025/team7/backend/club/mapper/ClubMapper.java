package com.capstone2025.team7.backend.club.mapper;

import com.capstone2025.team7.backend.category.entity.Category;
import com.capstone2025.team7.backend.club.dto.ClubDto;
import com.capstone2025.team7.backend.club.entity.Club;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ClubMapper {

    //Post Dto -> Entity
    @Mapping(target = "clubId", ignore = true)
    @Mapping(target = "category", source = "category") // category는 controller/service에서 조회해서 주입해야 함
    @Mapping(target = "clubCurrentPopulation", constant = "0")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Club postToClub(ClubDto.Post dto, Category category);

    // Entity → Response DTO
    @Mapping(source = "category.categoryId", target = "categoryId")
    ClubDto.Response clubToResponse(Club club);

    // Entity 리스트 → Response 리스트
    List<ClubDto.Response> clubsToResponses(List<Club> clubs);
}
