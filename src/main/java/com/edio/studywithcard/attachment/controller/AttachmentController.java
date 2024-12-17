package com.edio.studywithcard.attachment.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.attachment.service.AttachmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Attachment", description = "Attachment 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    /**
     * @param filePath
     * @ 테스트
     */
    @DeleteMapping("/attachment")
    public void deleteFile(@RequestParam("filePath") String filePath) {
        attachmentService.deleteAttachment(filePath);
    }
}
