// ClubMapper.java (수정본)
package com.capstone2025.team7.backend.club.mapper;

import com.capstone2025.team7.backend.category.entity.Category;
import com.capstone2025.team7.backend.club.dto.ClubDto;
import com.capstone2025.team7.backend.club.entity.Club;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClubMapper {

    @Mapping(target = "clubId", ignore = true)
    @Mapping(target = "category.categoryId", source = "categoryId")
    @Mapping(target = "clubCurrentPopulation", constant = "0")
    @Mapping(target = "isActive", constant = "false")
    @Mapping(target = "votes", ignore = true)
    @Mapping(target = "userClubList", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Club postDtoToEntity(ClubDto.Post postDto);

    // Post DTO -> Entity (Category 포함)
    @Mapping(target = "clubId", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "clubCurrentPopulation", constant = "0")
    @Mapping(target = "isActive", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "votes", ignore = true)
    Club postDtoToEntity(ClubDto.Post postDto, Category category);

    // Entity -> Response DTO (Service에서 entityToResponseDto로 호출)
    @Mapping(target = "categoryId", source = "category.categoryId")
    ClubDto.Response entityToResponseDto(Club club);

    // Entity 리스트 -> Response 리스트 (Service에서 entitiesToResponseDtos로 호출)
    List<ClubDto.Response> entitiesToResponseDtos(List<Club> clubs);

    // Patch DTO -> Entity 업데이트 (카테고리 변경하는 경우)
    @Mapping(target = "clubId", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "clubCurrentPopulation", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "votes", ignore = true)
    void updateClubFromPatch(ClubDto.Patch patchDto, Category category, @MappingTarget Club club);

    // Patch DTO -> Entity 업데이트 (카테고리 변경 없는 경우)
    @Mapping(target = "clubId", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "clubCurrentPopulation", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "votes", ignore = true)
    void updateClubFromPatch(ClubDto.Patch patchDto, @MappingTarget Club club);
}