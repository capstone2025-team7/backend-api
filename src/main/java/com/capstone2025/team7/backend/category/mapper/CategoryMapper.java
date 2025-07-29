package com.capstone2025.team7.backend.category.mapper;

import com.capstone2025.team7.backend.category.dto.CategoryDto;
import com.capstone2025.team7.backend.category.entity.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "categoryGroupId", target = "categoryGroup.categoryGroupId")
    Category postToCategory(CategoryDto.Post dto);

    @Mapping(source = "categoryGroup.categoryGroupId", target = "categoryGroupId")
    CategoryDto.Response categoryToResponse(Category category);

    List<CategoryDto.Response> categoriesToResponses(List<Category> categories);
}
