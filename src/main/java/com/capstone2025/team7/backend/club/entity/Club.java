package com.capstone2025.team7.backend.club.entity;

import com.capstone2025.team7.backend.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "club")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Club {

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

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false, length = 50)
    private Location location;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    public enum Location {
        SEOUL, BUSAN, DAEGU, INCHEON, GWANGJU, DAEJEON, ULSAN, JEJU, GYEONGGIDO
        // 필요에 따라 실제 활동 지역으로 수정
    }
}
