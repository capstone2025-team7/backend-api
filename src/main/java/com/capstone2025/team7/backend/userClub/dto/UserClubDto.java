package com.capstone2025.team7.backend.userClub.dto;

import com.capstone2025.team7.backend.club.entity.Club;
import com.capstone2025.team7.backend.user.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserClubDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Post {
        private long userId;
        private long clubId;

        @NotNull
        private String nickname;

//        public User getUser(){
//            User user = new User();
//            user.setUserId(userId);
//            return user;
//        }


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
    }

}
