package com.capstone2025.team7.backend.categoryGroup.entity;

import com.capstone2025.team7.backend.auditable.Auditable;
import com.capstone2025.team7.backend.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "category_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryGroup extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_group_id")
    private Long categoryGroupId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "categoryGroup", cascade = CascadeType.ALL)
    private List<Category> categories;
}
