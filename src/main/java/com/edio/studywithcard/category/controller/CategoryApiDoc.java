package com.edio.studywithcard.category.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.category.domain.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Category", description = "Category 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
public interface CategoryApiDoc {
    @Operation(summary = "Category 조회", description = "Category를 조회합니다.")
    List<Category> getCategories();
}
