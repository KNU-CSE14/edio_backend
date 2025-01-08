package com.edio.studywithcard.folder.service;

import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.AccountFolderResponse;
import com.edio.studywithcard.folder.model.response.FolderAllResponse;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.model.response.FolderWithDeckResponse;

import java.util.List;

public interface FolderService {
    /**
     * Folder 하위 1depth 조회
     *
     * @param accountId
     * @param folderId
     * @return
     */
    FolderWithDeckResponse getFolderWithDeck(Long accountId, Long folderId);

    FolderAllResponse getAllFolders(Long accountId, Long folderId);

    /**
     * 사용자 Folder 목록 조회
     *
     * @param accountId
     * @return
     */
    List<AccountFolderResponse> getAccountFolders(Long accountId);

    /**
     * Folder 이동
     *
     * @param folderId
     * @param newParentId
     */
    void moveFolder(Long folderId, Long newParentId);

    /**
     * Folder 등록
     *
     * @param accountId
     * @param folderCreateRequest
     * @return
     */
    FolderResponse createFolder(Long accountId, FolderCreateRequest folderCreateRequest);

    /**
     * Folder 수정(name)
     *
     * @param folderId
     * @param folderUpdateRequest
     */
    void updateFolder(Long folderId, FolderUpdateRequest folderUpdateRequest);

    /**
     * Folder 삭제
     *
     * @param folderId
     */
    void deleteFolder(Long folderId);
}
