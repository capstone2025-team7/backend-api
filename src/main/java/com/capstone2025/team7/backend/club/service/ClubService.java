package com.capstone2025.team7.backend.club.service;

import com.capstone2025.team7.backend.category.entity.Category;
import com.capstone2025.team7.backend.category.repository.CategoryRepository;
import com.capstone2025.team7.backend.club.dto.ClubDto;
import com.capstone2025.team7.backend.club.entity.Club;
import com.capstone2025.team7.backend.club.mapper.ClubMapper;
import com.capstone2025.team7.backend.club.repository.ClubRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final CategoryRepository categoryRepository;
    private final ClubMapper clubMapper;

    /**
     * 동호회 생성
     */
    public ClubDto.Response createClub(ClubDto.Post postDto) {
        // categoryId로 Category 조회
        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        // DTO → Entity 변환
        Club club = clubMapper.postToClub(postDto, category);

        // 저장
        Club savedClub = clubRepository.save(club);

        return clubMapper.clubToResponse(savedClub);
    }

    /**
     * 동호회 전체 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ClubDto.Response> getAllClubs() {
        List<Club> clubs = clubRepository.findAll();
        return clubMapper.clubsToResponses(clubs);
    }

    /**
     * 특정 동호회 상세 조회
     */
    @Transactional(readOnly = true)
    public ClubDto.Response getClubById(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동호회가 존재하지 않습니다."));
        return clubMapper.clubToResponse(club);
    }




    @Transactional
    public ClubDto.Response updateClub(Long clubId, ClubDto.Patch patchDto) {
        Club existingClub = clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("클럽을 찾을 수 없습니다."));

        // 카테고리 변경이 있는 경우
        if (patchDto.getCategoryId() != null &&
                !patchDto.getCategoryId().equals(existingClub.getCategory().getCategoryId())) {

            Category newCategory = categoryRepository.findById(patchDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

            clubMapper.updateClubFromPatch(patchDto, newCategory, existingClub);
        } else {
            // 카테고리 변경이 없는 경우
            clubMapper.updateClubFromPatch(patchDto, existingClub);
        }

        Club updatedClub = clubRepository.save(existingClub);
        return clubMapper.clubToResponse(updatedClub);
    }

    /**
     * 동호회 삭제
     */
    public void deleteClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당 동호회가 존재하지 않습니다."));

        clubRepository.delete(club);
    }
}
