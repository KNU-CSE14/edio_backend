package com.edio.studywithcard.folder.model.request;

public record FolderCreateRequest(
        Long parentId,
        String name
) {
}