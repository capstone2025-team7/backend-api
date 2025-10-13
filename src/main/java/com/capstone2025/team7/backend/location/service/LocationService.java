package com.capstone2025.team7.backend.location.service;

import com.capstone2025.team7.backend.location.entity.Location;
import com.capstone2025.team7.backend.location.repository.LocationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    public Location createLocation(Location newLocationData) {
        return locationRepository.findByName(newLocationData.getName())
                .map(location -> {
                    System.out.println("기존 location을 찾았습니다. 업데이트 합니다.");
                    location.setAddress(newLocationData.getAddress());
                    location.setImageUrl(newLocationData.getImageUrl());
                    location.setReservationUrl(newLocationData.getReservationUrl());
                    location.setOperatingHours(newLocationData.getOperatingHours());
                    location.setReservationPeriod(newLocationData.getReservationPeriod());
                    location.setCost(newLocationData.getCost());
                    return locationRepository.save(location);
                })
                .orElseGet(() -> {
                    System.out.println("location을 새로 생성합니다.");
                    return locationRepository.save(newLocationData);
                });
    }
}
