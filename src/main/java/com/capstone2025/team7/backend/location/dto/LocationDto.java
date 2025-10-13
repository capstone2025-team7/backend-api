package com.capstone2025.team7.backend.location.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto {
    private String name;
    private String address;
    private String imageUrl;
    private String reservationUrl;
    private String operatingHours;
    private String reservationPeriod;
    private String cost;
}
