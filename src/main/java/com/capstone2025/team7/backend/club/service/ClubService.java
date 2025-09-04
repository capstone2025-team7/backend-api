// ClubService.java
package com.capstone2025.team7.backend.club.service;

import com.capstone2025.team7.backend.club.dto.ClubDto;
import com.capstone2025.team7.backend.club.entity.Club;
import com.capstone2025.team7.backend.club.mapper.ClubMapper;
import com.capstone2025.team7.backend.club.repository.ClubRepository;
import com.capstone2025.team7.backend.exception.BusinessLogicException;
import com.capstone2025.team7.backend.exception.ExceptionCode;
import com.capstone2025.team7.backend.user.entity.User;
import com.capstone2025.team7.backend.user.repository.UserRepository;
import com.capstone2025.team7.backend.userClub.dto.UserClubDto;
import com.capstone2025.team7.backend.userClub.entity.UserClub;
import com.capstone2025.team7.backend.userClub.mapper.UserClubMapper;
import com.capstone2025.team7.backend.userClub.repository.UserClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final UserClubRepository userClubRepository;
    private final ClubMapper clubMapper;
    private final UserClubMapper userClubMapper;

    @Transactional
    public ClubDto.Response createClub(ClubDto.Post postDto) {
        Club club = clubMapper.postDtoToEntity(postDto);
        Club savedClub = clubRepository.save(club);
        return clubMapper.entityToResponseDto(savedClub);
    }

    public List<ClubDto.Response> getAllClubs() {
        List<Club> clubs = clubRepository.findAll();
        return clubMapper.entitiesToResponseDtos(clubs);
    }

    public ClubDto.Response getClubById(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));
        return clubMapper.entityToResponseDto(club);
    }

    @Transactional
    public ClubDto.Response updateClub(Long clubId, ClubDto.Patch patchDto) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));

        clubMapper.updateClubFromPatch(patchDto, club);

        Club updatedClub = clubRepository.save(club);
        return clubMapper.entityToResponseDto(updatedClub);
    }

    @Transactional
    public void deleteClub(Long clubId) {
        if (!clubRepository.existsById(clubId)) {
            throw new RuntimeException("Club not found with id: " + clubId);
        }
        clubRepository.deleteById(clubId);
    }

    @Transactional
    public UserClubDto.Response joinClub(UserClubDto.Post postDto) {
        if (userClubRepository.existsByUser_UserIdAndClub_ClubId(postDto.getUserId(), postDto.getClubId())) {
            throw new RuntimeException("Already applied to this club");
        }

        Club club = clubRepository.findById(postDto.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        User user = userRepository.findById(postDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserClub userClub = new UserClub();
        userClub.addUser(user);
        userClub.addClub(club);
        userClub.getUserClubStatuses().add(UserClub.UserClubStatus.USER_CLUB_STATUS_WAIT);

        userClub = userClubRepository.save(userClub);

        checkAndActivateClub(postDto.getClubId());

        return userClubMapper.entityToResponseDto(userClub);
    }

    public List<UserClubDto.Response> getClubUsers(Long clubId) {
        List<UserClub> members = userClubRepository.findByClub_ClubId(clubId);
        return userClubMapper.entitiesToResponseDtos(members);
    }

    // ClubService 맨 아래에 추가
    public Club findVerifiedClub(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CLUB_NOT_FOUND));
    }

    @Transactional
    public void removeUser(Long clubId, Long userClubId, Long requestUserId) {
        UserClub userClub = userClubRepository.findById(userClubId)
                .orElseThrow(() -> new RuntimeException("UserClub not found"));

        userClubRepository.delete(userClub);
    }

    @Transactional
    public void checkAndActivateClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        if (club.getIsActive()) {
            return;
        }

        Long pendingCount = userClubRepository.countPendingMembers(clubId,
                UserClub.UserClubStatus.USER_CLUB_STATUS_WAIT);

        if (pendingCount >= club.getMinUser()) {
            activateClubAndUsers(clubId);
        }
    }

    @Transactional
    public void activateClubAndUsers(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));
        club.setIsActive(true);
        clubRepository.save(club);

        List<UserClub> pendingMembers = userClubRepository.findByClub_ClubId(clubId);

        for (UserClub userClub : pendingMembers) {
            if (userClub.getUserClubStatuses().contains(UserClub.UserClubStatus.USER_CLUB_STATUS_WAIT)) {
                userClub.getUserClubStatuses().remove(UserClub.UserClubStatus.USER_CLUB_STATUS_WAIT);
                userClub.getUserClubStatuses().add(UserClub.UserClubStatus.USER_CLUB_STATUS_ACTIVE);
            }
        }

        userClubRepository.saveAll(pendingMembers);
    }

    public List<ClubDto.Response> getActiveClubs() {
        List<Club> allClubs = clubRepository.findAll();
        List<Club> activeClubs = allClubs.stream()
                .filter(club -> club.getIsActive())
                .toList();
        return clubMapper.entitiesToResponseDtos(activeClubs);
    }

    public List<ClubDto.Response> getInactiveClubs() {
        List<Club> allClubs = clubRepository.findAll();
        List<Club> inactiveClubs = allClubs.stream()
                .filter(club -> !club.getIsActive())
                .toList();
        return clubMapper.entitiesToResponseDtos(inactiveClubs);
    }
}