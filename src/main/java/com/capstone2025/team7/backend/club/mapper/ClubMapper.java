package com.capstone2025.team7.backend.club.mapper;

import com.capstone2025.team7.backend.category.entity.Category;
import com.capstone2025.team7.backend.club.dto.ClubDto;
import com.capstone2025.team7.backend.club.entity.Club;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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

    // Patch DTO -> Entity 업데이트 (null 값은 무시)
    @Mapping(target = "clubId", ignore = true)
    @Mapping(target = "category", source = "category") // 카테고리 변경 시 새로운 Category 객체 주입
    @Mapping(target = "clubCurrentPopulation", ignore = true) // 현재 인원은 수정하지 않음
    @Mapping(target = "isActive", ignore = true) // 활성 상태는 별도 메서드로 관리
    @Mapping(target = "createdAt", ignore = true) // 생성일은 수정하지 않음
//    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateClubFromPatch(ClubDto.Patch patchDto, Category category, @MappingTarget Club club);

    // 카테고리 변경 없이 업데이트하는 경우 (카테고리ID가 null이거나 기존과 동일한 경우)
    @Mapping(target = "clubId", ignore = true)
    @Mapping(target = "category", ignore = true) // 카테고리는 그대로 유지
    @Mapping(target = "clubCurrentPopulation", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateClubFromPatch(ClubDto.Patch patchDto, @MappingTarget Club club);

}
