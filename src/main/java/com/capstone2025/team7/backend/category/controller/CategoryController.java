package com.capstone2025.team7.backend.category.controller;

import com.capstone2025.team7.backend.category.dto.CategoryDto;
import com.capstone2025.team7.backend.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "카테고리 생성")
    public ResponseEntity<CategoryDto.Response> createCategory(@Valid @RequestBody CategoryDto.Post postDto) {
        return ResponseEntity.ok(categoryService.createCategory(postDto));
    }

    @GetMapping
    @Operation(summary = "카테고리 전체 조회")
    public ResponseEntity<List<CategoryDto.Response>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "단일 카테고리 조회")
    public ResponseEntity<CategoryDto.Response> getCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }
}
