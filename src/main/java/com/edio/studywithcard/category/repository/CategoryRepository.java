package com.edio.studywithcard.category.repository;

import com.edio.studywithcard.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByIsDeleted(boolean isDeleted);
}