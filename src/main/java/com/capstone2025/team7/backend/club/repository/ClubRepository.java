package com.capstone2025.team7.backend.club.repository;


import com.capstone2025.team7.backend.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubRepository extends JpaRepository<Club, Long> {
    // ClubRepository에 추가
    List<Club> findByIsActiveTrue();
    List<Club> findByIsActiveFalse();
}
