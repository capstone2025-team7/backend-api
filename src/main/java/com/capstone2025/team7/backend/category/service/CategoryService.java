package com.capstone2025.team7.backend.category.service;

import com.capstone2025.team7.backend.category.dto.CategoryDto;
import com.capstone2025.team7.backend.category.mapper.CategoryMapper;
import com.capstone2025.team7.backend.category.repository.CategoryRepository;

import com.capstone2025.team7.backend.categoryGroup.repository.CategoryGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryGroupRepository categoryGroupRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto.Response createCategory(CategoryDto.Post postDto) {
        var group = categoryGroupRepository.findById(postDto.getCategoryGroupId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리 그룹이 존재하지 않습니다."));
        var category = categoryMapper.postToCategory(postDto);
        category.setCategoryGroup(group);
        var saved = categoryRepository.save(category);
        return categoryMapper.categoryToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto.Response> getAllCategories() {
        return categoryMapper.categoriesToResponses(categoryRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CategoryDto.Response getCategoryById(Long categoryId) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));
        return categoryMapper.categoryToResponse(category);
    }
}
