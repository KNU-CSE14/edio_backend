package com.edio.studywithcard.attachment.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.attachment.domain.Attachment;
import com.edio.studywithcard.attachment.service.AttachmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
     * @param file
     * @param folder
     * @return
     * @throws IOException
     * @ 테스트
     */
    @PostMapping("/attachment")
    public Attachment uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) throws IOException {
        return attachmentService.saveAttachment(file, folder);
    }

    /**
     * @param filePath
     * @ 테스트
     */
    @DeleteMapping("/attachment")
    public void deleteFile(@RequestParam("filePath") String filePath) {
        attachmentService.deleteAttachment(filePath);
    }
}
