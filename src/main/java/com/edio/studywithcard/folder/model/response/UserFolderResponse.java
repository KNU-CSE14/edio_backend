package com.edio.studywithcard.folder.model.response;

import com.edio.studywithcard.folder.domain.Folder;

public record UserFolderResponse(
        Long id,
        String name
) {
    public static UserFolderResponse from(Folder folder) {
        return new UserFolderResponse(
                folder.getId(),
                folder.getName()
        );
    }
}
