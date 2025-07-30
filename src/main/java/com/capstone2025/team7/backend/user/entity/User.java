package com.capstone2025.team7.backend.user.entity;

import com.capstone2025.team7.backend.auditable.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
