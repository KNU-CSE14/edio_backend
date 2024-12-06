package com.edio.studywithcard.category.service;

import com.edio.studywithcard.category.domain.Category;

import java.util.List;

public interface CategoryService {
    // Category 전체 조회
    List<Category> getCategories();
}
