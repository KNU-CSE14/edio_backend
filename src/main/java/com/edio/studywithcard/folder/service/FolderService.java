package com.edio.studywithcard.folder.service;

import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.response.FolderResponse;

import java.util.List;

public interface FolderService {
    // Folder 조회
    List<FolderResponse> findOneFolder(Long accountId);
    // Folder 등록
    FolderResponse createFolder(Folder folder);
    // Folder 수정(이름)
    void updateFolder(Long id, Folder folder);
    // Folder 삭제
    void deleteFolder(Long id);
}
