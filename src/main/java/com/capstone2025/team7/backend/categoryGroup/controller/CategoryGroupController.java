package com.capstone2025.team7.backend.categoryGroup.controller;


import com.capstone2025.team7.backend.categoryGroup.dto.CategoryGroupDto;
import com.capstone2025.team7.backend.categoryGroup.service.CategoryGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @DeleteMapping("/{categoryGroupId}")
    @Operation(summary = "카테고리 그룹 삭제", description = "지정된 카테고리 그룹을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 그룹 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 그룹을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "해당 카테고리 그룹에 카테고리가 존재하여 삭제할 수 없음")
    })
    public ResponseEntity<Void> deleteCategoryGroup(
            @Parameter(description = "삭제할 카테고리 그룹 ID", required = true, example = "1")
            @PathVariable Long categoryGroupId) {
        categoryGroupService.deleteCategoryGroup(categoryGroupId);
        return ResponseEntity.ok().build();
    }
}
