package com.capstone2025.team7.backend.userClub.dto;

import com.capstone2025.team7.backend.club.entity.Club;
import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.userClub.entity.UserClub;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;

@Getter
public class UserClubDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Post {
        private long userId;
        private long clubId;

        @NotNull(message = "닉네임은 필수입니다.")
        private String nickname;

        @NotEmpty(message = "활동 가능 요일은 최소 1개 이상 선택해야 합니다.")
        private List<String> selectedDays;

        public User getUser(){
            User user = new User();
            user.setUserId(userId);
            return user;
        }

        public Club getClub() {
            Club club = new Club();
            club.setClubId(clubId);
            return club;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response {
        private long userClubId;
        private long userId;
        private String nickname;
        private String clubName;
        private long age;
        private List<UserClub.UserClubStatus> userClubStatusList;
        private List<DayOfWeek> selectedDays;
    }
}