package com.edio.studywithcard.card.model.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CardBulkRequest {
    private Long deckId;
    private Long cardId;
    private String name;
    private String description;
    private MultipartFile[] files;

    @Override
    public String toString() {
        List<String> fileNames = Arrays.stream(files)
                .map(MultipartFile::getOriginalFilename)
                .toList();
        return "CardBulkRequest{" +
                "deckId=" + deckId +
                ", cardId=" + cardId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", files=" + fileNames +
                '}';
    }
}
