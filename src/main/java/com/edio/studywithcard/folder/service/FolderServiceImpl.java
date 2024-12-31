package com.edio.studywithcard.folder.service;

import com.edio.common.exception.custom.BadRequestException;
import com.edio.common.exception.custom.ConflictException;
import com.edio.common.exception.custom.NotFoundException;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.model.response.FolderWithDeckResponse;
import com.edio.studywithcard.folder.model.response.UserFolderResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.user.domain.Account;
import com.edio.user.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;

    private final AccountRepository accountRepository;

    /*
        Folder 조회
     */
    @Override
    @Transactional(readOnly = true)
    public FolderWithDeckResponse getFolderWithDeck(Long accountId, Long folderId) {
        Folder folder;
        // folderId가 null이면 루트 폴더 조회
        if (folderId == null) {
            Long rootFolderId = accountRepository.findById(accountId)
                    .map(Account::getRootFolderId)
                    .orElseThrow(() -> new NotFoundException(Account.class, accountId));
            folder = folderRepository.findById(rootFolderId)
                    .orElseThrow(() -> new NotFoundException(Folder.class, rootFolderId));
        } else {
            folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new NotFoundException(Folder.class, folderId));
        }
        return FolderWithDeckResponse.from(folder);
    }

    /*
       사용자 Folder 목록 조회
    */
    @Override
    @Transactional(readOnly = true)
    public List<UserFolderResponse> getUserFolders(Long accountId) {
        List<Folder> folders = folderRepository.findByAccountIdAndIsDeletedFalse(accountId);

        return folders.stream()
                .map(UserFolderResponse::from)
                .toList();
    }

    /*
        Folder 등록
     */
    @Override
    @Transactional
    public FolderResponse createFolder(Long accoutId, FolderCreateRequest folderCreateRequest) {
        try {
            // 부모 폴더 설정
            Folder parentFolder = null;
            if (folderCreateRequest.parentId() != null) {
                parentFolder = folderRepository.getReferenceById(folderCreateRequest.parentId());
            }

            Folder newFolder = Folder.builder()
                    .accountId(accoutId)
                    .parentFolder(parentFolder) // 부모 폴더 설정
                    .name(folderCreateRequest.name())
                    .build();
            Folder savedFolder = folderRepository.save(newFolder);
            return FolderResponse.from(savedFolder);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(Folder.class, folderCreateRequest.name());
        }
    }

    /*
        Folder명 수정
     */
    @Override
    @Transactional
    public void updateFolder(Long id, FolderUpdateRequest folderUpdateRequest) {
        // 폴더명 업데이트
        Folder existingFolder = folderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(Folder.class, id));

        existingFolder.setName(folderUpdateRequest.name());
    }

    /*
        Folder 이동
     */
    @Override
    @Transactional
    public void moveFolder(Long folderId, Long newParentId) {
        // 이동할 폴더 조회
        Folder folder = folderRepository.findByIdAndIsDeletedFalse(folderId)
                .orElseThrow(() -> new NotFoundException(Folder.class, folderId));

        // 새로운 부모 폴더 조회
        Folder newParentFolder = null;
        if (newParentId != null) {
            newParentFolder = folderRepository.getReferenceById(newParentId);
        }

        // 사이클 방지: 새로운 부모 폴더가 이동 대상 폴더의 하위인지 확인
        if (isDescendant(folder, newParentFolder)) {
            throw new BadRequestException(Folder.class, newParentId);
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
    public void deleteFolder(Long id) {
        Folder existingFolder = folderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException(Folder.class, id));

        existingFolder.setDeleted(true);
    }
}
