package com.capstone2025.team7.backend.userClub.entity;

import com.capstone2025.team7.backend.auditable.Auditable;
import com.capstone2025.team7.backend.club.entity.Club;
import com.capstone2025.team7.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
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

    @Column(name = "nickname", length = 50)
    private String nickname;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Column(name = "selected_day")
    private List<DayOfWeek> selectedDays = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<UserClubStatus> userClubStatuses = new ArrayList<>();

    public void addUser(User user) {
        this.user = user;
        if (user != null && !user.getUserClubList().contains(this)) {
            user.addUserClub(this);
        }
    }

    public void addClub(Club club) {
        this.club = club;
        if (club != null && !club.getUserClubList().contains(this)) {
            club.addUserClub(this);
        }
    }

    public enum UserClubStatus {
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