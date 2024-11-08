package com.edio.studywithcard.folder.model.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FolderUpdateRequest {
    Long parentId;
    String name;
}