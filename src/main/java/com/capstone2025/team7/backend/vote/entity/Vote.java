package com.capstone2025.team7.backend.vote.entity;

import com.capstone2025.team7.backend.auditable.Auditable;
import com.capstone2025.team7.backend.club.entity.Club;
import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.userClub.entity.UserClub;
import com.capstone2025.team7.backend.userVote.entity.UserVote;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "description", length = 50, nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteStatus voteStatus = VoteStatus.VOTE_STATUS_ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.PERSIST)
    List<UserVote> userVoteList = new ArrayList<>();

    public void addUserVote(UserVote userVote){
        this.userVoteList.add(userVote);
        if(userVote.getVote() != this){
            userVote.addVote(this);
        }
    }

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
