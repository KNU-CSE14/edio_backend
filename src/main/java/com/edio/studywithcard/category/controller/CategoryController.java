package com.edio.studywithcard.category.controller;

import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController implements CategoryApiDoc {

    private final CategoryService categoryService;

    @GetMapping("/category")
    @Override
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }
}
