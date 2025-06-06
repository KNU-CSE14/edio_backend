package com.edio.studywithcard.folder.service;

import com.edio.common.exception.base.ErrorMessages;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.AccountFolderResponse;
import com.edio.studywithcard.folder.model.response.FolderAllResponse;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;

    /*
        Folder 조회 (all depth)
     */
    @Override
    @Transactional(readOnly = true)
    public FolderAllResponse getAllFolders(Long rootFolderId, Long folderId) {
        Long targetFolderId = (folderId == null) ? rootFolderId : folderId;

        Folder folder = folderRepository.findById(targetFolderId).get();

        return FolderAllResponse.from(folder);
    }


    /*
       사용자 Folder 목록 조회
    */
    @Override
    @Transactional(readOnly = true)
    public List<AccountFolderResponse> getAccountFolders(Long accountId) {
        List<Folder> folders = folderRepository.findByAccountIdAndIsDeletedFalse(accountId);

        return folders.stream()
                .map(AccountFolderResponse::from)
                .toList();
    }

    /*
        Folder 등록
     */
    @Override
    @Transactional
    public FolderResponse createFolder(Long accountId, FolderCreateRequest folderCreateRequest) {
        // 부모 폴더 설정
        Folder parentFolder = null;
        if (folderCreateRequest.parentId() != null) {
            parentFolder = folderRepository.getReferenceById(folderCreateRequest.parentId());
        }

        Folder newFolder = Folder.builder()
                .accountId(accountId)
                .parentFolder(parentFolder) // 부모 폴더 설정
                .name(folderCreateRequest.name())
                .build();
        Folder savedFolder = folderRepository.save(newFolder);
        return FolderResponse.from(savedFolder);
    }

    /*
        Folder명 수정
     */
    @Override
    @Transactional
    public void updateFolder(Long folderId, FolderUpdateRequest folderUpdateRequest) {
        // 폴더명 업데이트
        Folder existingFolder = folderRepository.findByIdAndIsDeletedFalse(folderId).get();

        existingFolder.setName(folderUpdateRequest.name());
    }

    /*
        Folder 이동
     */
    @Override
    @Transactional
    public void moveFolder(Long folderId, Long newParentId) {
        // 이동할 폴더 조회
        Folder folder = folderRepository.findByIdAndIsDeletedFalse(folderId).get();

        // 새로운 부모 폴더 조회
        Folder newParentFolder = null;
        if (newParentId != null) {
            newParentFolder = folderRepository.getReferenceById(newParentId);
        }

        // 사이클 방지: 새로운 부모 폴더가 이동 대상 폴더의 하위인지 확인
        if (isDescendant(folder, newParentFolder)) {
            throw new IllegalArgumentException(ErrorMessages.BAD_REQUEST.getMessage());
        }

        folder.setParentFolder(newParentFolder);
    }

    private boolean isDescendant(Folder targetFolder, Folder newParentFolder) {
        // 부모 폴더를 타고 올라가며 사이클 여부 확인
        while (newParentFolder != null) {
            if (newParentFolder.getId().equals(targetFolder.getId())) {
                return true;
            }
            newParentFolder = newParentFolder.getParentFolder();
        }
        return false;
    }

    /*
        Folder 삭제
     */
    @Override
    @Transactional
    public void deleteFolder(Long folderId) {
        Folder existingFolder = folderRepository.findByIdAndIsDeletedFalse(folderId).get();

        if (existingFolder.getParentFolder() == null) {
            throw new IllegalArgumentException(ErrorMessages.BAD_REQUEST.getMessage());
        }
        folderRepository.delete(existingFolder);
    }
}
