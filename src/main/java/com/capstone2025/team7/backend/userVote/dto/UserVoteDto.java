package com.capstone2025.team7.backend.userVote.dto;

import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.vote.entity.Vote;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class UserVoteDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Post {
        private Long userId;
        private Long voteId;

        public User getUser() {
            User user = new User();
            user.setUserId(userId);
            return user;
        }

        public Vote getVote() {
            Vote vote = new Vote();
            vote.setVoteId(voteId);
            return vote;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response {
        private Long userVoteId;
        private Long userId;
        private String userName;
        private String userEmail;
        private Long voteId;
        private String voteTitle;
        private String voteDescription; // <- 이 필드가 있나요?

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime modifiedAt;
    }
}