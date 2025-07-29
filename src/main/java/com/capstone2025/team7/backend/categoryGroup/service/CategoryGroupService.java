package com.capstone2025.team7.backend.categoryGroup.service;


import com.capstone2025.team7.backend.category.repository.CategoryRepository;
import com.capstone2025.team7.backend.categoryGroup.dto.CategoryGroupDto;
import com.capstone2025.team7.backend.categoryGroup.entity.CategoryGroup;
import com.capstone2025.team7.backend.categoryGroup.mapper.CategoryGroupMapper;
import com.capstone2025.team7.backend.categoryGroup.repository.CategoryGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryGroupService {

    private final CategoryGroupRepository categoryGroupRepository;
    private final CategoryGroupMapper categoryGroupMapper;
    private final CategoryRepository categoryRepository;

    public CategoryGroupDto.Response createCategoryGroup(CategoryGroupDto.Post postDto) {
        CategoryGroup group = categoryGroupMapper.postToCategoryGroup(postDto);
        CategoryGroup saved = categoryGroupRepository.save(group);
        return categoryGroupMapper.categoryGroupToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryGroupDto.Response> getAllGroups() {
        return categoryGroupMapper.groupsToResponses(categoryGroupRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CategoryGroupDto.Response getGroupById(Long id) {
        CategoryGroup group = categoryGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리 그룹이 존재하지 않습니다."));
        return categoryGroupMapper.categoryGroupToResponse(group);
    }

    public void deleteCategoryGroup(Long categoryGroupId) {
        // 1. 카테고리 그룹 존재 여부 확인
        CategoryGroup categoryGroup = categoryGroupRepository.findById(categoryGroupId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리 그룹을 찾을 수 없습니다."));

        // 2. 해당 카테고리 그룹에 속한 카테고리가 있는지 확인 (있다면 삭제 불가)
        if (categoryRepository.existsByCategoryGroupId(categoryGroupId)) {
            throw new IllegalStateException("해당 카테고리 그룹에 카테고리가 존재하여 삭제할 수 없습니다.");
        }

        // 3. 카테고리 그룹 삭제
        categoryGroupRepository.delete(categoryGroup);
    }
}
