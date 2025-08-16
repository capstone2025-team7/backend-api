package com.capstone2025.team7.backend.vote.entity;

import com.capstone2025.team7.backend.auditable.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Vote extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id", nullable = false)
    private long voteId;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "descriptions", length = 50, nullable = false)
    private String descriptions;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteStatus voteStatus = VoteStatus.VOTE_STATUS_ACTIVE;

    public enum VoteStatus {
        VOTE_STATUS_ACTIVE("활성"),
        VOTE_STATUS_COMPLETE("종료");

        @Getter
        private String status;

        VoteStatus(String status){
            this.status = status;
        }
    }

}
