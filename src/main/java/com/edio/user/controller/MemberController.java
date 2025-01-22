package com.edio.user.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Members", description = "Members 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {
}
