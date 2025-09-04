package com.capstone2025.team7.backend.vote.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

public class VoteDto {

    /**
     * 투표 생성용 DTO (POST)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Post {
        @NotNull(message = "사용자 ID는 필수입니다.")
        private Long userId;

        @NotNull(message = "클럽 ID는 필수입니다.")
        private Long clubId;

        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @NotBlank(message = "설명은 필수입니다.")
        private String description;

        @NotNull(message = "시작일은 필수입니다.")
        private LocalDateTime startDate;

        @NotNull(message = "종료일은 필수입니다.")
        private LocalDateTime dueDate;
    }

    /**
     * 투표 수정용 DTO (PATCH)
     * null 값은 업데이트하지 않도록 서비스/매퍼에서 처리
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Patch {
        private Long voteId;
        private String title;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime dueDate;
    }

    /**
     * 응답 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long voteId;
        private Long userId;
        private Long clubId;
        private String clubName;
        private String title;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime dueDate;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}