package com.capstone2025.team7.backend.club.entity;

import com.capstone2025.team7.backend.auditable.Auditable;
import com.capstone2025.team7.backend.category.entity.Category;
import com.capstone2025.team7.backend.userClub.entity.UserClub;
import com.capstone2025.team7.backend.vote.entity.Vote;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "club")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Club extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id", nullable = false)
    private Long clubId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "club_name", nullable = false, length = 100)
    private String clubName;

    @Column(nullable = false)
    private int clubTotalPopulation;

    @Column(nullable = false)
    private int clubCurrentPopulation = 0;

    @Column(nullable = false)
    private int minUser;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false, length = 50)
    private Location location;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    // Club 엔티티에 요일 필드 추가
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_day")
    private DayOfWeek activityDay;

    @Column(name = "parent_club_name")
    private String parentClubName; // "탁구", "등산" 등

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<Vote> votes;

    @OneToMany(mappedBy = "club", cascade = CascadeType.PERSIST)
    List<UserClub> userClubList = new ArrayList<>();

    public void addUserClub(UserClub userClub){
        this.userClubList.add(userClub);
        if(userClub.getClub() != this){
            userClub.addClub(this);
        }
    }


    public enum Location {
        SEOUL, BUSAN, DAEGU, INCHEON, GWANGJU, DAEJEON, ULSAN, JEJU, GYEONGGIDO
        // 필요에 따라 실제 활동 지역으로 수정
    }
}
