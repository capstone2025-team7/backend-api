package com.capstone2025.team7.backend.categoryGroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CategoryGroupDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long categoryGroupId;
        private String name;
    }
}
