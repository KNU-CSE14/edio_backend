package com.edio.studywithcard.folder.model.response;

import com.edio.studywithcard.folder.domain.Folder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class FolderResponse {
    private Long id;
    //    private Long accountId;
    private Long parentId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //    private boolean isDeleted;
    private List<FolderResponse> childrenFolders = new ArrayList<>();

    public static FolderResponse from(Folder folder) {
        List<FolderResponse> childrenResponses = folder.getChildrenFolders().stream()
                .filter(child -> !child.isDeleted()) // 삭제되지 않은 자식 필터링
                .map(FolderResponse::from) // 재귀적으로 Folder -> FolderResponse 변환
                .sorted(Comparator.comparing(FolderResponse::getUpdatedAt).reversed())
                .collect(Collectors.toList());

        return new FolderResponse(
                folder.getId(),
                folder.getParentFolder() != null ? folder.getParentFolder().getId() : null,
                folder.getName(),
                folder.getCreatedAt(),
                folder.getUpdatedAt(),
                childrenResponses
        );
    }
}
