package com.edio.studywithcard.folder.service;

import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.model.response.FolderWithDeckResponse;

public interface FolderService {
    // Folder 조회
//    List<FolderResponse> getFolders(Long accountId, Long folderId);

    // Folder 하위 조회(Folder, Deck)
    FolderWithDeckResponse getFolderWithDeck(Long accountId, Long folderId);

    // Folder 등록
    FolderResponse createFolder(FolderCreateRequest folderCreateRequest);

    // Folder 수정(이름)
    void updateFolder(Long id, FolderUpdateRequest folderUpdateRequest);

    // Folder 삭제
    void deleteFolder(Long id);
}
