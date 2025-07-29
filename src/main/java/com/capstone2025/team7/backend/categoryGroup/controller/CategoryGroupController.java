package com.capstone2025.team7.backend.categoryGroup.controller;


import com.capstone2025.team7.backend.categoryGroup.dto.CategoryGroupDto;
import com.capstone2025.team7.backend.categoryGroup.service.CategoryGroupService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category-groups")
@RequiredArgsConstructor
public class CategoryGroupController {

    private final CategoryGroupService categoryGroupService;

    @PostMapping
    @Operation(summary = "카테고리 그룹 생성")
    public ResponseEntity<CategoryGroupDto.Response> createGroup(@Valid @RequestBody CategoryGroupDto.Post dto) {
        return ResponseEntity.ok(categoryGroupService.createCategoryGroup(dto));
    }

    @GetMapping
    @Operation(summary = "카테고리 그룹 전체 조회")
    public ResponseEntity<List<CategoryGroupDto.Response>> getAllGroups() {
        return ResponseEntity.ok(categoryGroupService.getAllGroups());
    }

    @GetMapping("/{id}")
    @Operation(summary = "단일 카테고리 그룹 조회")
    public ResponseEntity<CategoryGroupDto.Response> getGroup(@PathVariable Long id) {
        return ResponseEntity.ok(categoryGroupService.getGroupById(id));
    }
}
