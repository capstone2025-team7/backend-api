package com.capstone2025.team7.backend.categoryGroup.service;


import com.capstone2025.team7.backend.categoryGroup.dto.CategoryGroupDto;
import com.capstone2025.team7.backend.categoryGroup.entity.CategoryGroup;
import com.capstone2025.team7.backend.categoryGroup.mapper.CategoryGroupMapper;
import com.capstone2025.team7.backend.categoryGroup.repository.CategoryGroupRepository;
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
}
