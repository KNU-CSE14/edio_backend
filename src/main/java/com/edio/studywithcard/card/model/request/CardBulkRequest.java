package com.edio.studywithcard.card.model.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile image;
    private MultipartFile audio;

    @Override
    public String toString() {
        return "CardBulkRequest{" +
                "deckId=" + deckId +
                ", cardId=" + cardId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image=" + (image != null ? image.getOriginalFilename() : "null") +
                ", audio=" + (audio != null ? audio.getOriginalFilename() : "null") +
                '}';
    }
}
