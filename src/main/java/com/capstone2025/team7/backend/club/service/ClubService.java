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
import java.util.Optional;
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

    // ClubService.java 내 joinClub 메서드 전체

    @Transactional
    public UserClubDto.Response joinClub(UserClubDto.Post postDto) {
        // 1. 유효성 검사 및 초기 설정
        if (userClubRepository.existsByUser_UserIdAndClub_ClubId(postDto.getUserId(), postDto.getClubId())) {
            throw new RuntimeException("Already applied to this club");
        }

        Club originalClub = findVerifiedClub(postDto.getClubId());
        User user = userRepository.findById(postDto.getUserId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        List<DayOfWeek> selectedDays = postDto.getSelectedDays().stream()
                .map(this::convertStringToDayOfWeek)
                .collect(Collectors.toList());

        List<DayOfWeek> daysSuccessfullyJoined = new ArrayList<>();
        UserClub finalReturnEntry = null;

        // 2. 모든 selectedDays를 순회하며 ACTIVE 클럽이 있는지 확인하고 모두 가입 처리
        for (DayOfWeek day : selectedDays) {
            Optional<Club> optionalExistingDayClub = clubRepository.findByParentClubNameAndActivityDayAndIsActive(
                    originalClub.getClubName(), day, true);

            if (optionalExistingDayClub.isPresent()) {
                Club existingDayClub = optionalExistingDayClub.get();

                // 동호회가 꽉 차지 않았는지 확인
                if (existingDayClub.getClubCurrentPopulation() < existingDayClub.getClubTotalPopulation()) {

                    // --- Multi-Join Logic: 성공한 요일마다 새로운 UserClub 생성 ---
                    UserClub activeUserClub = new UserClub();
                    activeUserClub.setUser(user);
                    activeUserClub.setClub(existingDayClub); // Link to the specific day club (Tuesday/Friday)
                    activeUserClub.setNickname(postDto.getNickname());
                    activeUserClub.setSelectedDays(List.of(day)); // Only the specific day
                    activeUserClub.getUserClubStatuses().add(UserClub.UserClubStatus.USER_CLUB_STATUS_ACTIVE);
                    userClubRepository.save(activeUserClub);

                    existingDayClub.setClubCurrentPopulation(existingDayClub.getClubCurrentPopulation() + 1);
                    clubRepository.save(existingDayClub);

                    daysSuccessfullyJoined.add(day); // 성공한 요일 기록
                    finalReturnEntry = activeUserClub; // 마지막으로 가입된 엔티티를 반환용으로 저장
                }
            }
        }

        // 3. PENDING Day 계산
        List<DayOfWeek> pendingDays = selectedDays.stream()
                .filter(day -> !daysSuccessfullyJoined.contains(day))
                .collect(Collectors.toList());

        // 4. 남은 요일에 대해 PENDING UserClub 생성
        if (!pendingDays.isEmpty()) {
            UserClub pendingUserClub = new UserClub();
            pendingUserClub.addUser(user);
            pendingUserClub.addClub(originalClub);
            pendingUserClub.setNickname(postDto.getNickname());
            pendingUserClub.setSelectedDays(pendingDays); // 남은 요일만 저장
            pendingUserClub.getUserClubStatuses().add(UserClub.UserClubStatus.USER_CLUB_STATUS_WAIT);
            finalReturnEntry = userClubRepository.save(pendingUserClub);
        }

        // 5. PENDING이 발생했거나 (새로운 클럽 생성을 유도해야 하므로) 체크 로직 호출
        if (!pendingDays.isEmpty() || !daysSuccessfullyJoined.isEmpty()) {
            checkAndCreateClubsByDay(originalClub.getClubId());
        }

        // 6. 최종 반환
        if (finalReturnEntry == null) {
            // 모든 신청 요일이 꽉 찬 클럽이었고 pending도 0인 경우에만 발생 (예외 처리 필요)
            throw new BusinessLogicException(ExceptionCode.CLUB_POPULATION_FULL);
        }

        return userClubMapper.entityToResponseDto(finalReturnEntry);
    }

    // ClubService.java 내부에 추가/수정될 메서드

    @Transactional
    public void withdrawClub(Long clubId, Long userClubId, Long requestUserId) {
        // 1. UserClub 엔티티 유효성 검사 및 권한 확인
        UserClub userClub = userClubRepository.findById(userClubId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_CLUB_NOT_FOUND));

        // 클럽ID 일치 여부 확인
        if (!userClub.getClub().getClubId().equals(clubId)) {
            throw new BusinessLogicException(ExceptionCode.CLUB_MISMATCH);
        }

        // [권한 로직]: 요청자가 해당 UserClub의 주인인지 확인 (자가 탈퇴/취소 시나리오)
        if (!userClub.getUser().getUserId().equals(requestUserId)) {
            // 관리자 권한 확인 로직이 없다면, 본인만 취소/탈퇴 가능
            throw new BusinessLogicException(ExceptionCode.NO_PERMISSION);
        }

        // 2. 상태 확인 및 인원수 감소 처리 (ACTIVE 상태일 경우에만 인원 감소 = 탈퇴)
        if (userClub.getUserClubStatuses().contains(UserClub.UserClubStatus.USER_CLUB_STATUS_ACTIVE)) {
            // 🚩 활성 동호회 탈퇴 (인원수 감소) 로직
            Club club = userClub.getClub();

            // 인원수가 1 이상인 경우에만 감소 (0 미만이 되는 것을 방지)
            if (club.getClubCurrentPopulation() > 0) {
                club.setClubCurrentPopulation(club.getClubCurrentPopulation() - 1);
                clubRepository.save(club);
            }
        } else if (userClub.getUserClubStatuses().contains(UserClub.UserClubStatus.USER_CLUB_STATUS_WAIT)) {
            // 🚩 가입 신청 취소 (대기열에서 이탈) 로직: 인원수 감소 없음
            // 추가적인 비즈니스 로직 없이 바로 삭제합니다.
        }

        // 3. UserClub 엔티티 삭제 (Hard Delete)
        userClubRepository.delete(userClub);

        // TODO: (선택 사항) 만약 userClubStatus를 WITHDRAWN 등으로 변경하는 Soft Delete를 원하시면 로직 변경 필요.
    }



    // --- checkAndCreateClubsByDay 및 관련 메서드 ---
    @Transactional
    public void checkAndCreateClubsByDay(Long originalClubId) {
        Club originalClub = findVerifiedClub(originalClubId);

        // 해당 클럽의 모든 대기자들 조회 (WAIT 상태)
        List<UserClub> pendingMembers = userClubRepository.findByClub_ClubIdAndUserClubStatuses(
                originalClubId, UserClub.UserClubStatus.USER_CLUB_STATUS_WAIT);

        // 요일별로 그룹핑
        Map<DayOfWeek, List<UserClub>> dayGroups = groupUsersByDay(pendingMembers);

        // 각 요일 그룹이 최소인원을 만족하는지 확인 및 동호회 생성
        for (Map.Entry<DayOfWeek, List<UserClub>> entry : dayGroups.entrySet()) {
            DayOfWeek day = entry.getKey();
            List<UserClub> dayMembers = entry.getValue();

            // 최소 인원 만족 확인
            if (dayMembers.size() >= originalClub.getMinUser()) {

                // 새로운 요일별 동호회 생성 (또는 분점 생성)
                Club newDayClub = createDaySpecificClub(originalClub, day);

                // 해당 요일 멤버들을 새 클럽으로 이동 및 원본 대기 정보 업데이트/삭제
                // createdDay를 넘겨주어 해당 요일만 처리하도록 함.
                moveUsersToNewClub(dayMembers, newDayClub, day);
            }
        }
    }

    private Map<DayOfWeek, List<UserClub>> groupUsersByDay(List<UserClub> pendingMembers) {
        return pendingMembers.stream()
                .flatMap(userClub -> userClub.getSelectedDays().stream()
                        .map(selectedDay -> Map.entry(selectedDay, userClub)))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    @Transactional
    public Club createDaySpecificClub(Club originalClub, DayOfWeek dayOfWeek) {
        // ClubRepository에 `countByParentClubNameAndActivityDayAndIsActive` 메서드가 필요합니다.
        long existingCount = clubRepository.countByParentClubNameAndActivityDayAndIsActive(
                originalClub.getClubName(), dayOfWeek, true
        );

        String newClubNameSuffix = (existingCount > 0) ? " " + (existingCount + 1) : "";

        Club dayClub = new Club();
        dayClub.setCategory(originalClub.getCategory());
        dayClub.setClubName(originalClub.getClubName() + newClubNameSuffix + "(" + getDayName(dayOfWeek) + ")");
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
    public void moveUsersToNewClub(List<UserClub> dayMembers, Club newClub, DayOfWeek createdDay) {
        int movedCount = 0;

        // Map을 사용하여 UserClubId를 기준으로 유니크한 원본 UserClub 엔티티만 처리합니다.
        Map<Long, UserClub> uniqueUserClubs = dayMembers.stream()
                .collect(Collectors.toMap(UserClub::getUserClubId, uc -> uc, (existing, replacement) -> existing));

        for (UserClub originalUserClub : uniqueUserClubs.values()) {

            // 1. 새 동호회(요일별/분점)에 대한 새로운 UserClub (Active) 엔티티 생성
            UserClub activeUserClub = new UserClub();
            activeUserClub.setUser(originalUserClub.getUser());
            activeUserClub.setClub(newClub); // 새 동호회와 연결
            activeUserClub.setNickname(originalUserClub.getNickname());

            // 새 엔티티에는 성공적으로 가입된 요일만 포함
            activeUserClub.setSelectedDays(List.of(createdDay));
            activeUserClub.getUserClubStatuses().add(UserClub.UserClubStatus.USER_CLUB_STATUS_ACTIVE);
            userClubRepository.save(activeUserClub);
            movedCount++;

            // 2. 기존 대기 엔티티 (originalUserClub) 업데이트
            // 성공한 요일을 원본 대기 엔티티의 selectedDays에서 제거
            originalUserClub.getSelectedDays().remove(createdDay);

            if (originalUserClub.getSelectedDays().isEmpty()) {
                // 더 이상 대기할 요일이 없으면 원본 UserClub (WAIT 상태) 삭제
                userClubRepository.delete(originalUserClub);
            } else {
                // 남은 요일이 있으면 대기 상태 (WAIT)를 유지하고 업데이트
                userClubRepository.save(originalUserClub);
            }
        }

        // 새 클럽의 현재 인원 수 업데이트
        newClub.setClubCurrentPopulation(newClub.getClubCurrentPopulation() + movedCount);
        clubRepository.save(newClub);
    }
    // --- checkAndCreateClubsByDay 및 관련 메서드 수정 끝 ---

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
        // userClubId로 UserClub 엔티티를 찾습니다.
        UserClub userClub = userClubRepository.findById(userClubId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_CLUB_NOT_FOUND));

        // 1. 요청한 사용자가 해당 UserClub의 소유자인지 확인
        if (!userClub.getUser().getUserId().equals(requestUserId)) {
            // 소유자가 아니면 권한 없음 예외 발생
            throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED_USER);
        }

        // 2. 권한이 확인되면 정상적으로 삭제 진행
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