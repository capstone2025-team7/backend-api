package com.capstone2025.team7.backend.categoryGroup.mapper;


import com.capstone2025.team7.backend.categoryGroup.dto.CategoryGroupDto;
import com.capstone2025.team7.backend.categoryGroup.entity.CategoryGroup;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryGroupMapper {
    CategoryGroup postToCategoryGroup(CategoryGroupDto.Post dto);
    CategoryGroupDto.Response categoryGroupToResponse(CategoryGroup group);
    List<CategoryGroupDto.Response> groupsToResponses(List<CategoryGroup> list);
}
