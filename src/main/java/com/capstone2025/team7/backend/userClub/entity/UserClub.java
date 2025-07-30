package com.capstone2025.team7.backend.userClub.entity;

import com.capstone2025.team7.backend.auditable.Auditable;
import com.capstone2025.team7.backend.club.entity.Club;
import com.capstone2025.team7.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
