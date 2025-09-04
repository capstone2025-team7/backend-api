package com.capstone2025.team7.backend.vote.service;

import com.capstone2025.team7.backend.club.entity.Club;
import com.capstone2025.team7.backend.club.service.ClubService;
import com.capstone2025.team7.backend.exception.BusinessLogicException;
import com.capstone2025.team7.backend.exception.ExceptionCode;
import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.user.service.UserService;
import com.capstone2025.team7.backend.vote.dto.VoteDto;
import com.capstone2025.team7.backend.vote.entity.Vote;
import com.capstone2025.team7.backend.vote.mapper.VoteMapper;
import com.capstone2025.team7.backend.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteMapper voteMapper;
    private final ClubService clubService;
    private final UserService userService;

    public VoteDto.Response createVote(VoteDto.Post postDto) {
        // 날짜 검증
        validateDates(postDto.getStartDate(), postDto.getDueDate());

        // 클럽 및 사용자 존재 확인
        Club club = clubService.findVerifiedClub(postDto.getClubId());
        User user = userService.findVerifiedUser(postDto.getUserId());

        Vote vote = new Vote();
        vote.setTitle(postDto.getTitle());
        vote.setDescription(postDto.getDescription()); // DTO의 description -> Entity의 descriptions
        vote.setStartDate(postDto.getStartDate());
        vote.setDueDate(postDto.getDueDate());
        vote.setClub(club);
        vote.setUser(user);
        vote.setVoteStatus(Vote.VoteStatus.VOTE_STATUS_ACTIVE);

        Vote savedVote = voteRepository.save(vote);
        return voteMapper.voteToResponseDto(savedVote);
    }

    @Transactional(readOnly = true)
    public VoteDto.Response findVote(Long voteId) {
        Vote vote = findVerifiedVote(voteId);
        return voteMapper.voteToResponseDto(vote);
    }

    @Transactional(readOnly = true)
    public Page<VoteDto.Response> findVotes(Long clubId, Long userId, Pageable pageable) {
        Page<Vote> votes;

        if (clubId != null && userId != null) {
            votes = voteRepository.findByClubClubIdAndUserUserId(clubId, userId, pageable);
        } else if (clubId != null) {
            votes = voteRepository.findByClubClubId(clubId, pageable);
        } else if (userId != null) {
            votes = voteRepository.findByUserUserId(userId, pageable);
        } else {
            votes = voteRepository.findAll(pageable);
        }

        return votes.map(voteMapper::voteToResponseDto);
    }

    public VoteDto.Response updateVote(VoteDto.Patch patchDto) {
        Vote vote = findVerifiedVote(patchDto.getVoteId());

        // 날짜가 제공된 경우에만 검증
        LocalDateTime startDate = patchDto.getStartDate() != null ? patchDto.getStartDate() : vote.getStartDate();
        LocalDateTime dueDate = patchDto.getDueDate() != null ? patchDto.getDueDate() : vote.getDueDate();
        validateDates(startDate, dueDate);

        // 업데이트
        if (patchDto.getTitle() != null) {
            vote.setTitle(patchDto.getTitle());
        }
        if (patchDto.getDescription() != null) {
            vote.setDescription(patchDto.getDescription()); // DTO의 description -> Entity의 descriptions
        }
        if (patchDto.getStartDate() != null) {
            vote.setStartDate(patchDto.getStartDate());
        }
        if (patchDto.getDueDate() != null) {
            vote.setDueDate(patchDto.getDueDate());
        }

        return voteMapper.voteToResponseDto(vote);
    }

    public void deleteVote(Long voteId) {
        Vote vote = findVerifiedVote(voteId);
        voteRepository.delete(vote);
    }

    public VoteDto.Response completeVote(Long voteId) {
        Vote vote = findVerifiedVote(voteId);
        vote.setVoteStatus(Vote.VoteStatus.VOTE_STATUS_COMPLETE);
        return voteMapper.voteToResponseDto(vote);
    }

    @Transactional(readOnly = true)
    public Page<VoteDto.Response> findVotesByClub(Long clubId, Pageable pageable) {
        // 클럽 존재 확인
        clubService.findVerifiedClub(clubId);

        Page<Vote> votes = voteRepository.findByClubClubId(clubId, pageable);
        return votes.map(voteMapper::voteToResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<VoteDto.Response> findVotesByUser(Long userId, Pageable pageable) {
        // 사용자 존재 확인
        userService.findVerifiedUser(userId);

        Page<Vote> votes = voteRepository.findByUserUserId(userId, pageable);
        return votes.map(voteMapper::voteToResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<VoteDto.Response> findActiveVotes(Pageable pageable) {
        Page<Vote> votes = voteRepository.findByVoteStatus(Vote.VoteStatus.VOTE_STATUS_ACTIVE, pageable);
        return votes.map(voteMapper::voteToResponseDto);
    }

    @Transactional(readOnly = true)
    public Vote findVerifiedVote(Long voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.VOTE_NOT_FOUND));
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime dueDate) {
        if (startDate.isAfter(dueDate)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_DATE_RANGE);
        }

        if (dueDate.isBefore(LocalDateTime.now())) {
            throw new BusinessLogicException(ExceptionCode.PAST_DUE_DATE);
        }
    }
}