package com.capstone2025.team7.backend.facility.mapper;

import com.capstone2025.team7.backend.facility.dto.FacilityInfo;
import com.capstone2025.team7.backend.facility.entity.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FacilityMapper {

    FacilityMapper INSTANCE = Mappers.getMapper(FacilityMapper.class);

    Facility toEntity(FacilityInfo dto);

    void updateFromDto(FacilityInfo dto, @MappingTarget Facility entity);
}
