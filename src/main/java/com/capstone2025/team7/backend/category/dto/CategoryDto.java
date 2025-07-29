package com.capstone2025.team7.backend.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CategoryDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        private String name;
        private Long categoryGroupId;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long categoryId;
        private String name;
        private Long categoryGroupId;
    }
}
