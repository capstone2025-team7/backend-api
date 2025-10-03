package com.capstone2025.team7.backend.facility.repository;

import com.capstone2025.team7.backend.facility.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    Optional<Facility> findByName(String name);
}
