package com.capstone2025.team7.backend.user.dto;

import com.capstone2025.team7.backend.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        @NotNull
        private String name;

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
    }
}
