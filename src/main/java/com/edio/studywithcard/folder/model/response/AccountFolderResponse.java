package com.edio.studywithcard.folder.model.response;

import com.edio.studywithcard.folder.domain.Folder;

public record AccountFolderResponse(
        Long id,
        String name
) {
    public static AccountFolderResponse from(Folder folder) {
        return new AccountFolderResponse(
                folder.getId(),
                folder.getName()
        );
    }
}
