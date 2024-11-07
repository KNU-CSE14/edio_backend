package com.edio.studywithcard.folder.model.response;

import com.edio.studywithcard.folder.domain.Folder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class FolderResponse {
    private Long id;
    private Long accountId;
    private Long parentId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean status;
    private List<FolderResponse> children = new ArrayList<>(); // 초기값으로 빈 리스트 설정

    public static FolderResponse from(Folder folder) {
        return new FolderResponse(
                folder.getId(),
                folder.getAccountId(),
                folder.getParentId(),
                folder.getName(),
                folder.getCreatedAt(),
                folder.getUpdatedAt(),
                folder.isStatus(),
                new ArrayList<>()
        );
    }
}
