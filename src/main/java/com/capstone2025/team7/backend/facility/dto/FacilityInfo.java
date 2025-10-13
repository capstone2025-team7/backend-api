package com.capstone2025.team7.backend.facility.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FacilityInfo {
    private String name;
    private String address;
    private String imageUrl;
    private String reservationUrl;
    private String operatingHours;
    private String reservationPeriod;
    private String cost;
    private String phone;
    private String category;
}
