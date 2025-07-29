package com.capstone2025.team7.backend.category.repository;

import com.capstone2025.team7.backend.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
