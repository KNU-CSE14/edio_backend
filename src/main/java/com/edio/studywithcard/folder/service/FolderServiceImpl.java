package com.edio.studywithcard.folder.service;

import com.edio.common.exception.ConflictException;
import com.edio.common.exception.NotFoundException;
import com.edio.studywithcard.deck.domain.Deck;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.model.response.FolderWithDeckResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.user.domain.Account;
import com.edio.user.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Optional;

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
        Folder 등록
     */
    @Override
    @Transactional
    public FolderResponse createFolder(Long accoutId, FolderCreateRequest folderCreateRequest) {
        try {
            // 부모 폴더 설정
            Folder parentFolder = null;
            if (folderCreateRequest.getParentId() != null) {
                parentFolder = entityManager.getReference(Folder.class, folderCreateRequest.getParentId());
            }

            Folder newFolder = Folder.builder()
                    .accountId(accoutId)
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
        Folder명 수정
     */
    @Override
    @Transactional
    public void updateFolder(Long id, FolderUpdateRequest folderUpdateRequest) {
        // 폴더명 업데이트
        Folder existingFolder = folderRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new NotFoundException(Folder.class, id));

        existingFolder.setName(folderUpdateRequest.getName());
    }

    /*
        Folder 이동(하위 폴더, 덱)
     */
    @Override
    @Transactional
    public void moveFolder(Long folderId, Long newParentId) {
        // 이동할 폴더 조회
        Folder folderToMove = folderRepository.findByIdAndIsDeleted(folderId, false)
                .orElseThrow(() -> new NotFoundException(Folder.class, folderId));

        // 새로운 부모 폴더 조회
        Folder newParentFolder = null;
        if (newParentId != null) {
            newParentFolder = entityManager.getReference(Folder.class, newParentId);
        }

        // 사이클 방지: 새로운 부모 폴더가 이동 대상 폴더의 하위인지 확인
        if (isDescendant(folderToMove, newParentFolder)) {
            throw new IllegalArgumentException("폴더를 자기 자신이나 하위 폴더로 이동할 수 없습니다.");
        }

        // 폴더 이동
        moveFolderAndChildren(folderToMove, newParentFolder);
    }

    /*
        폴더 이동 무한 루프 사이클 방지 메서드
     */
    private boolean isDescendant(Folder targetFolder, Folder newParentFolder) {
        // 부모 폴더를 타고 올라가며 사이클 여부 확인
        while (newParentFolder != null) {
            if (newParentFolder.getId().equals(targetFolder.getId())) {
                return true; // 사이클 발생
            }
            newParentFolder = newParentFolder.getParentFolder();
        }
        return false; // 사이클 없음
    }

    /*
        하위 폴더 이동 재귀 메서드
     */
    private void moveFolderAndChildren(Folder rootFolder, Folder newParentFolder) {
        // 스택을 사용한 반복 처리
        Deque<Folder> stack = new ArrayDeque<>();
        stack.push(rootFolder);

        // 부모 폴더 설정
        rootFolder.setParentFolder(newParentFolder);

        while (!stack.isEmpty()) {
            Folder currentFolder = stack.pop();

            // 현재 폴더의 모든 하위 덱을 새로운 부모 폴더로 이동
            if (newParentFolder != null) {
                for (Deck deck : currentFolder.getDecks()) {
                    deck.setFolder(newParentFolder);
                }
            }

            // 스택에 하위 폴더 추가 (null 안전 처리)
            for (Folder childFolder : Optional.ofNullable(currentFolder.getChildrenFolders())
                    .orElse(Collections.emptyList())) {
                stack.push(childFolder);
                childFolder.setParentFolder(currentFolder); // 부모 설정
            }
        }
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
