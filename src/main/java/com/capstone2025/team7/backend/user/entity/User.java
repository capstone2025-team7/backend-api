package com.capstone2025.team7.backend.user.entity;

import com.capstone2025.team7.backend.auditable.Auditable;
import com.capstone2025.team7.backend.userClub.entity.UserClub;
import com.capstone2025.team7.backend.userVote.entity.UserVote;
import com.capstone2025.team7.backend.vote.entity.Vote;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "profile_image", length = 255, nullable = false)
    private String profileImage;

    @Column(name = "age", length = 100, nullable = false)
    private int age;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender", length = 20, nullable = false)
    private gender gender;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private userRole role = userRole.MEMBER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    List<UserClub> userClubList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    List<UserVote> userVoteList = new ArrayList<>();

    public void addUserClub(UserClub userClub){
        this.userClubList.add(userClub);
        if(userClub.getUser() != this){
            userClub.addUser(this);
        }
    }

    public void addUserVote(UserVote userVote){
        this.userVoteList.add(userVote);
        if(userVote.getUser() != this){
            userVote.addUser(this);
        }
    }

    @Getter
    public enum gender {
        MALE("남자"),
        FEMALE("여자");

        private final String value;

        gender(String value) {
            this.value = value;
        }
    }

    @Getter
    public enum userRole {
        ADMIN("관리자"),
        MEMBER("유저");

        private final String value;

        userRole(String value) {
            this.value = value;
        }
    }
}
