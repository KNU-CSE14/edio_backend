package com.edio.studywithcard.category.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.category.domain.Category;
import com.edio.studywithcard.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Category", description = "Category 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Category 조회", description = "Category를 조회합니다.")
    @GetMapping("/category")
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }
}
