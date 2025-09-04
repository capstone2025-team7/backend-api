package com.capstone2025.team7.backend.userClub.entity;

import com.capstone2025.team7.backend.auditable.Auditable;
import com.capstone2025.team7.backend.club.entity.Club;
import com.capstone2025.team7.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserClub extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userClubId;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "CLUB_ID")
    private Club club;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<UserClubStatus> userClubStatuses = new ArrayList<>();

    public void addUser(User user){
        this.user = user;
        if(!this.user.getUserClubList().contains(this)){
            this.user.addUserClub(this);
        }
    }

    public void addClub(Club club){
        this.club = club;
        if(!this.club.getUserClubList().contains(this)){
            this.club.addUserClub(this);
        }
    }

    public enum UserClubStatus{
        USER_CLUB_STATUS_ACTIVE(1, "활동 회원"),
        USER_CLUB_STATUS_INACTIVE(2, "탈퇴 회원"),
        USER_CLUB_STATUS_WAIT(3, "가입 대기 회원");

        @Getter
        private int userClubStatusNumber;

        @Getter
        private String userClubStatusDescription;

        UserClubStatus(int userClubStatusNumber, String userClubStatusDescription) {
            this.userClubStatusNumber = userClubStatusNumber;
            this.userClubStatusDescription = userClubStatusDescription;
        }
    }
}
