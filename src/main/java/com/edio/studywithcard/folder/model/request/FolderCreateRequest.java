package com.edio.studywithcard.folder.model.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FolderCreateRequest {
    Long accountId;
    Long parentId;
    String name;
}