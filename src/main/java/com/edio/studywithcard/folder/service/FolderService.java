package com.edio.studywithcard.folder.service;

import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.model.response.FolderWithDeckResponse;
import com.edio.studywithcard.folder.model.response.UserFolderResponse;

import java.util.List;

public interface FolderService {
    // Folder 조회
    FolderWithDeckResponse getFolderWithDeck(Long accountId, Long folderId);

    // 사용자 Folder 목록 조회
    List<UserFolderResponse> getUserFolders(Long accountId);

    // Folder 이동
    void moveFolder(Long folderId, Long newParentId);

    // Folder 등록
    FolderResponse createFolder(Long accountId, FolderCreateRequest folderCreateRequest);

    // Folder 수정(이름)
    void updateFolder(Long id, FolderUpdateRequest folderUpdateRequest);

    // Folder 삭제
    void deleteFolder(Long id);
}
