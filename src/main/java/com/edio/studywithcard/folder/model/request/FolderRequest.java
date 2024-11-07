package com.edio.studywithcard.folder.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FolderRequest {
    Long accountId;
    Long parentId;
    String name;
}