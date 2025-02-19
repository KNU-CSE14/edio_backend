package com.edio.studywithcard.card.dto;

import com.edio.studywithcard.card.domain.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class AttachmentBulkData {
    private MultipartFile file;
    private Card card;
    private String folder;
    private String target;
}
