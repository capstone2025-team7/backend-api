package com.capstone2025.team7.backend.category.repository;

import com.capstone2025.team7.backend.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.categoryGroup.categoryGroupId = :categoryGroupId")
    boolean existsByCategoryGroupId(@Param("categoryGroupId") Long categoryGroupId);

    @Query("SELECT c FROM Category c WHERE c.categoryGroup.categoryGroupId = :categoryGroupId")
    List<Category> findByCategoryGroupId(@Param("categoryGroupId") Long categoryGroupId);
}