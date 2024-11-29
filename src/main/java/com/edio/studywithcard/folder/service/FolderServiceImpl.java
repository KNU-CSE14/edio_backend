package com.edio.studywithcard.folder.service;

import com.edio.common.exception.ConflictException;
import com.edio.common.exception.NotFoundException;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.user.domain.Account;
import com.edio.user.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final EntityManager entityManager;

    private final FolderRepository folderRepository;

    private final AccountRepository accountRepository;

    /*
        Folder 조회
     */
    @Transactional(readOnly = true)
    @Override
    public List<FolderResponse> getFolders(Long accountId, Long folderId) {
        List<Folder> folders;
        if (folderId == null) {
            Long rootFolderId = accountRepository.findById(accountId)
                    .map(Account::getRootFolderId)
                    .orElseThrow(() -> new NotFoundException(Account.class, accountId));
            folders = folderRepository.findAllByAccountIdAndParentFolderIdAndIsDeleted(accountId, rootFolderId, false);
        } else {
            folders = folderRepository.findAllByAccountIdAndParentFolderIdAndIsDeleted(accountId, folderId, false);
        }

        return folders.stream()
                .map(FolderResponse::from)
                .sorted(Comparator.comparing(FolderResponse::getUpdatedAt).reversed())
                .collect(Collectors.toList());
    }

    /*
        Folder 등록
     */
    @Override
    @Transactional
    public FolderResponse createFolder(FolderCreateRequest folderCreateRequest) {
        try {
            // 부모 폴더 설정
            Folder parentFolder = null;
            if (folderCreateRequest.getParentId() != null) {
                parentFolder = entityManager.getReference(Folder.class, folderCreateRequest.getParentId());
            }

            Folder newFolder = Folder.builder()
                    .accountId(folderCreateRequest.getAccountId())
                    .parentFolder(parentFolder) // 부모 폴더 설정
                    .name(folderCreateRequest.getName())
                    .build();
            Folder savedFolder = folderRepository.save(newFolder);
            return FolderResponse.from(savedFolder);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(Folder.class, folderCreateRequest.getName());
        }
    }

    /*
        Folder 수정
     */
    @Override
    @Transactional
    public void updateFolder(Long id, FolderUpdateRequest folderUpdateRequest) {
        // 폴더명 업데이트
        Folder existingFolder = folderRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new NotFoundException(Folder.class, id));

        existingFolder.setName(folderUpdateRequest.getName());

        // 부모 폴더 업데이트
        Folder newParentFolder = null;
        if (folderUpdateRequest.getParentId() != null) {
            // 부모 폴더가 존재하는 경우 새로운 부모 폴더 조회
            newParentFolder = entityManager.getReference(Folder.class, folderUpdateRequest.getParentId());
        }

        // 부모 폴더 설정
        existingFolder.setParentFolder(newParentFolder);
    }

    /*
        Folder 삭제
     */
    @Override
    @Transactional
    public void deleteFolder(Long id) {
        Folder existingFolder = folderRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new NotFoundException(Folder.class, id));

        existingFolder.setDeleted(true);
    }
}
