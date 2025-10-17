package com.capstone2025.team7.backend.user.dto;

import com.capstone2025.team7.backend.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "새로운 사용자 생성을 위한 요청 DTO")
public class UserDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        @NotNull
        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;

        @NotBlank(message = "닉네임은 필수 항목입니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,8}$",
                message = "특수문자 제외 2자이상 8자 이하로 입력해주세요.")
        @Schema(description = "사용자 닉네임", example = "luke")
        private String nickname;

        @NotNull
        @Schema(description = "사용자 비번", example = "test123")
        private String password;

        @Schema(description = "사용자 프로필 이미지 주소", example = "profileImage")
        private String profileImage;

        @NotNull
        @Schema(description = "사용자 나이", example = "65")
        private int age;

        @NotNull
        @Schema(description = "사용자 성별", example = "MALE")
        private User.gender gender;

        @Email
        @Schema(description = "사용자 이메일", example = "test123@gmail.com")
        private String email;

        @NotEmpty(message = "활동 가능 요일은 최소 1개 이상 선택해야 합니다.")
        @Schema(description = "사용자 가능 요일", example =  "[\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\"]")
        private List<String> availableDays;

        @NotNull
        private String location;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Patch {
        private String email;

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
        private List<String> roles;
        private List<String> availableDays;
        private List<String> availableDaysKorean;
        private String location;

        public static UserResponse from(User user, List<String> availableDays, List<String> availableDaysKorean) {
            UserResponse response = new UserResponse();
            response.setUserId(user.getUserId());
            response.setName(user.getName());
            response.setNickname(user.getNickname());
            response.setEmail(user.getEmail());
            response.setProfileImage(user.getProfileImage());
            response.setAge(user.getAge());
            response.setGender(user.getGender());
            response.setRoles(user.getRoles());
            response.setAvailableDays(availableDays);
            response.setAvailableDaysKorean(availableDaysKorean);
            response.setLocation(user.getLocation());
            return response;
        }
    }
}
