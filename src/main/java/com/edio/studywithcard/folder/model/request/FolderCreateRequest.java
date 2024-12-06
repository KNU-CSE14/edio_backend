package com.edio.studywithcard.folder.model.request;

import lombok.Data;

@Data
public class FolderCreateRequest {
    private Long parentId;
    private String name;
}