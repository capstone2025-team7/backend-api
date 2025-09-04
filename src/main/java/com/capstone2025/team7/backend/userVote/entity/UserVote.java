package com.capstone2025.team7.backend.userVote.entity;


import com.capstone2025.team7.backend.auditable.Auditable;
import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.vote.entity.Vote;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "user_vote")
@Getter
@Setter
@NoArgsConstructor
public class UserVote extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_vote_id", nullable = false)
    private long userVoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VOTE_ID")
    private Vote vote;

    public void addUser(User user) {
        this.user = user;
        if (user != null && !user.getUserVoteList().contains(this)) {
            this.user.addUserVote(this);
        }
    }

    public void addVote(Vote vote) {
        this.vote = vote;
        if (vote != null && !vote.getUserVoteList().contains(this)) {
            this.vote.addUserVote(this);
        }
    }
}
