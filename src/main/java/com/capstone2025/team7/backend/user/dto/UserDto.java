package com.capstone2025.team7.backend.user.dto;

import com.capstone2025.team7.backend.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class UserDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        @NotNull
        private String name;

        @NotBlank(message = "닉네임은 필수 항목입니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,8}$",
                message = "특수문자 제외 2자이상 8자 이하로 입력해주세요.")
        private String nickname;

        @NotNull
        private String password;

        private String profileImage;

        @NotNull
        private int age;

        @NotNull
        private User.gender gender;

        @Email
        private String email;

        @NotEmpty(message = "활동 가능 요일은 최소 1개 이상 선택해야 합니다.")
        private List<String> availableDays;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Patch {
        private long userId;

        @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,8}$",
                message = "특수문자 제외 2자이상 8자 이하로 입력해주세요.")
        private String nickname;

        private String password;

        private String profileImage;

        private List<String> availableDays;
    }

    /**
     * 가능 요일 업데이트 요청 DTO
     */
    @Getter
    @Setter
    public static class AvailableDaysUpdateRequest {
        private List<String> availableDays; // ["MONDAY", "TUESDAY", "WEDNESDAY"]
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long userId;
        private String name;
        private String nickname;
        private String email;
        private String profileImage;
        private int age;
        private User.gender gender;
        private User.userRole role;
        private List<String> availableDays;
        private List<String> availableDaysKorean;

        public static UserResponse from(User user, List<String> availableDays, List<String> availableDaysKorean) {
            UserResponse response = new UserResponse();
            response.setUserId(user.getUserId());
            response.setName(user.getName());
            response.setNickname(user.getNickname());
            response.setEmail(user.getEmail());
            response.setProfileImage(user.getProfileImage());
            response.setAge(user.getAge());
            response.setGender(user.getGender());
            response.setRole(user.getRole());
            response.setAvailableDays(availableDays);
            response.setAvailableDaysKorean(availableDaysKorean);
            return response;
        }
    }
}
