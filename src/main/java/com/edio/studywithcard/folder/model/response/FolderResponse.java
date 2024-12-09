package com.edio.studywithcard.folder.model.response;

import com.edio.studywithcard.folder.domain.Folder;

import java.time.LocalDateTime;

public record FolderResponse(
        Long id,
        Long parentId,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FolderResponse from(Folder folder) {
        return new FolderResponse(
                folder.getId(),
                folder.getParentFolder() != null ? folder.getParentFolder().getId() : null,
                folder.getName(),
                folder.getCreatedAt(),
                folder.getUpdatedAt()
        );
    }
}
