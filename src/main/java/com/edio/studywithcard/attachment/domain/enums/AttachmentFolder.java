package com.edio.studywithcard.attachment.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AttachmentFolder {
    IMAGE("image"),
    AUDIO("audio");

    private final String value;
}
