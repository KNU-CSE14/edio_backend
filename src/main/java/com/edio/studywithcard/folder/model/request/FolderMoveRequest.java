package com.edio.studywithcard.folder.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FolderMoveRequest {
    private Long parentId;
}
