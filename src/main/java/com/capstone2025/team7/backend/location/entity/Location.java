package com.capstone2025.team7.backend.location.entity;

import com.capstone2025.team7.backend.auditable.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "location")
@Getter
@Setter
@NoArgsConstructor
public class Location extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false, unique = true)
    private Long locationId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;

    @Column(name = "reservationUrl", nullable = false)
    private String reservationUrl;

    @Column(name = "operatingHours", nullable = false)
    private String operatingHours;

    @Column(name = "reservationPeriod", nullable = false)
    private String reservationPeriod;

    @Column(name = "cost", nullable = false)
    private String cost;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "category", nullable = false)
    private String category;
}
