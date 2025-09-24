// ClubService.java
package com.capstone2025.team7.backend.club.service;

import com.capstone2025.team7.backend.category.entity.Category;
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

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Category category = new Category();
        category.setCategoryId(postDto.getCategoryId());
        club.setCategory(category);

        Club savedClub = clubRepository.save(club);
        return clubMapper.entityToResponseDto(savedClub);
    }

    private String getDayName(DayOfWeek day) {
        switch (day) {
            case MONDAY: return "월요일";
            case TUESDAY: return "화요일";
            case WEDNESDAY: return "수요일";
            case THURSDAY: return "목요일";
            case FRIDAY: return "금요일";
            case SATURDAY: return "토요일";
            case SUNDAY: return "일요일";
            default: return day.name();
        }
    }

    private DayOfWeek convertStringToDayOfWeek(String dayStr) {
        try {
            return DayOfWeek.valueOf(dayStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("잘못된 요일: " + dayStr);
        }
    }

    public List<ClubDto.Response> getAllClubs() {
        List<Club> clubs = clubRepository.findAll();
        return clubMapper.entitiesToResponseDtos(clubs);
    }

    public ClubDto.Response getClubById(Long clubId) {
        Club club = findVerifiedClub(clubId);
        return clubMapper.entityToResponseDto(club);
    }

    @Transactional
    public ClubDto.Response updateClub(Long clubId, ClubDto.Patch patchDto) {
        Club club = findVerifiedClub(clubId);
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

        Club club = findVerifiedClub(postDto.getClubId());
        User user = userRepository.findById(postDto.getUserId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        UserClub userClub = new UserClub();
        userClub.addUser(user);
        userClub.addClub(club);
        userClub.setNickname(postDto.getNickname());

        // 사용자가 선택한 활동 요일들을 UserClub에 저장
        List<DayOfWeek> selectedDays = postDto.getSelectedDays().stream()
                .map(this::convertStringToDayOfWeek)
                .collect(Collectors.toList());
        userClub.setSelectedDays(selectedDays);

        userClub.getUserClubStatuses().add(UserClub.UserClubStatus.USER_CLUB_STATUS_WAIT);
        userClub = userClubRepository.save(userClub);

        // 요일별 분할 생성 체크
        checkAndCreateClubsByDay(postDto.getClubId());

        return userClubMapper.entityToResponseDto(userClub);
    }

    @Transactional
    public void checkAndCreateClubsByDay(Long originalClubId) {
        Club originalClub = findVerifiedClub(originalClubId);

        // 이미 활성화된 클럽은 처리하지 않음
        if (originalClub.getIsActive()) {
            return;
        }

        // 해당 클럽의 모든 대기자들 조회
        List<UserClub> pendingMembers = userClubRepository.findByClub_ClubIdAndUserClubStatuses(
                originalClubId, UserClub.UserClubStatus.USER_CLUB_STATUS_WAIT);

        // 요일별로 그룹핑
        Map<DayOfWeek, List<UserClub>> dayGroups = groupUsersByDay(pendingMembers);

        // 각 요일 그룹이 최소인원을 만족하는지 확인 및 동호회 생성
        for (Map.Entry<DayOfWeek, List<UserClub>> entry : dayGroups.entrySet()) {
            List<UserClub> dayMembers = entry.getValue();

            if (dayMembers.size() >= originalClub.getMinUser()) {
                // 새로운 요일별 동호회 생성
                Club newDayClub = createDaySpecificClub(originalClub, entry.getKey());

                // 해당 요일 멤버들을 새 클럽으로 이동
                moveUsersToNewClub(dayMembers, newDayClub);
            }
        }
    }

    private Map<DayOfWeek, List<UserClub>> groupUsersByDay(List<UserClub> pendingMembers) {
        Map<DayOfWeek, List<UserClub>> dayGroups = Map.of(
                DayOfWeek.MONDAY, new ArrayList<>(),
                DayOfWeek.TUESDAY, new ArrayList<>(),
                DayOfWeek.WEDNESDAY, new ArrayList<>(),
                DayOfWeek.THURSDAY, new ArrayList<>(),
                DayOfWeek.FRIDAY, new ArrayList<>(),
                DayOfWeek.SATURDAY, new ArrayList<>(),
                DayOfWeek.SUNDAY, new ArrayList<>()
        );

        for (UserClub userClub : pendingMembers) {
            for (DayOfWeek selectedDay : userClub.getSelectedDays()) {
                dayGroups.get(selectedDay).add(userClub);
            }
        }

        return dayGroups;
    }

    @Transactional
    public Club createDaySpecificClub(Club originalClub, DayOfWeek dayOfWeek) {
        Club dayClub = new Club();
        dayClub.setCategory(originalClub.getCategory());
        dayClub.setClubName(originalClub.getClubName() + "(" + getDayName(dayOfWeek) + ")");
        dayClub.setClubTotalPopulation(originalClub.getClubTotalPopulation());
        dayClub.setMinUser(originalClub.getMinUser());
        dayClub.setDescription(originalClub.getDescription());
        dayClub.setLocation(originalClub.getLocation());
        dayClub.setIsActive(true);
        dayClub.setActivityDay(dayOfWeek);
        dayClub.setParentClubName(originalClub.getClubName());

        return clubRepository.save(dayClub);
    }

    @Transactional
    public void moveUsersToNewClub(List<UserClub> users, Club newClub) {
        for (UserClub userClub : users) {
            // 기존 UserClub 삭제
            userClubRepository.delete(userClub);

            // 새 UserClub 생성
            UserClub newUserClub = new UserClub();
            newUserClub.setUser(userClub.getUser());
            newUserClub.setClub(newClub);
            newUserClub.setNickname(userClub.getNickname());
            newUserClub.getUserClubStatuses().add(UserClub.UserClubStatus.USER_CLUB_STATUS_ACTIVE);

            userClubRepository.save(newUserClub);
        }

        // 새 클럽의 현재 인원 수 설정
        newClub.setClubCurrentPopulation(users.size());
        clubRepository.save(newClub);
    }

    public List<UserClubDto.Response> getClubUsers(Long clubId) {
        List<UserClub> members = userClubRepository.findByClub_ClubId(clubId);
        return userClubMapper.entitiesToResponseDtos(members);
    }

    public Club findVerifiedClub(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CLUB_NOT_FOUND));
    }

    @Transactional
    public void removeUser(Long clubId, Long userClubId, Long requestUserId) {
        UserClub userClub = userClubRepository.findById(userClubId)
                .orElseThrow(() -> new RuntimeException("UserClub not found"));

        userClubRepository.delete(userClub);

        // 현재 인원 수 업데이트
        Club club = findVerifiedClub(clubId);
        List<UserClub> activeMembers = userClubRepository.findByClub_ClubId(clubId);
        long activeCount = activeMembers.stream()
                .filter(uc -> uc.getUserClubStatuses().contains(UserClub.UserClubStatus.USER_CLUB_STATUS_ACTIVE))
                .count();

        club.setClubCurrentPopulation((int) activeCount);
        clubRepository.save(club);
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