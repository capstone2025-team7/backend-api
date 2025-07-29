package com.capstone2025.team7.backend.club.dto;

import com.capstone2025.team7.backend.club.entity.Club.Location;
import com.capstone2025.team7.backend.userClub.dto.UserClubDto;
import com.capstone2025.team7.backend.utils.validator.NotSpace;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;


import java.time.LocalDateTime;
import java.util.List;

public class ClubDto {

    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotNull(message = "카테고리 ID는 필수입니다.")
        private Long categoryId;

        @NotSpace
        @Pattern(regexp = "^[\\p{L}\\p{N}가-힣]{1,20}$", message = "동호회 이름은 특수문자, 공백 없이 1~20자여야 합니다.")
        private String clubName;

        @Range(min = 1, max = 100, message = "최대 인원 수는 1~100명이어야 합니다.")
        private int clubTotalPopulation;

        @Size(min = 1, max = 200, message = "동호회 소개는 1~200자 이내여야 합니다.")
        private String description;

        @NotNull(message = "지역은 필수입니다.")
        private Location location;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Patch {
        @NotNull(message = "동호회 ID는 필수입니다.")
        private Long clubId;

        @Range(min = 1, max = 100, message = "최대 인원 수는 1~100명이어야 합니다.")
        private int clubTotalPopulation;

        @Size(min = 1, max = 200, message = "동호회 소개는 1~200자 이내여야 합니다.")
        private String description;

        @NotNull(message = "활동 지역은 필수입니다.")
        private Location location;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Long clubId;
        private Long categoryId;
        private String clubName;
        private int clubTotalPopulation;
        private int clubCurrentPopulation;
        private String description;
        private Location location;
        private Boolean isActive;
        private LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    public static class ResponseWithMembers {
        private Long clubId;
        private Long categoryId;
        private String clubName;
        private int clubTotalPopulation;
        private int clubCurrentPopulation;
        private String description;
        private Location location;
        private Boolean isActive;
        private LocalDateTime createdAt;

        private List<UserClubDto.Response> users;
    }
}
