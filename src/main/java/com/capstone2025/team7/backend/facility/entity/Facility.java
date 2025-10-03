package com.capstone2025.team7.backend.facility.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_id", nullable = false)
    private Long id;

    @Column(name = "facility_name", unique = true, nullable = false)
    private String name;

    @Column(name = "facility_category", nullable = false)
    private String category;

    @Column(name = "facility_address", nullable = false)
    private String address;

    @Column(name = "facility_phone", nullable = false)
    private String phone;

    @Column(name = "facility_reservationUrl", nullable = false)
    private String reservationUrl;

    @Column(name = "facility_operatingHours", nullable = false)
    private String operatingHours;

    @Column(name = "facility_reservationPeriod", nullable = false, length = 1000)
    private String reservationPeriod;

    @Column(name = "facility_cost", nullable = false, length = 1000)
    private String cost;

    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;
}
