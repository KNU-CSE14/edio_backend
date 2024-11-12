package com.edio.studywithcard.folder.service;

import com.edio.common.exception.ConflictException;
import com.edio.common.exception.NotFoundException;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;

    public FolderServiceImpl(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    /*
        Folder 조회
     */
    @Transactional(readOnly = true)
    @Override
    public List<FolderResponse> findOneFolder(Long accountId) {
        List<Folder> rootFolders = folderRepository.findAllByAccountIdAndParentFolderIsNullAndIsDeleted(accountId, false);

        return rootFolders.stream()
                .map(this::convertToFolderResponse)
                .sorted((f1, f2) -> f2.getUpdatedAt().compareTo(f1.getUpdatedAt())) // 날짜 내림차순 정렬
                .collect(Collectors.toList());
    }

    private FolderResponse convertToFolderResponse(Folder folder) {
        FolderResponse folderResponse = FolderResponse.from(folder);
        List<FolderResponse> children = folder.getChildrenFolders().stream()
                .filter(child -> !child.isDeleted())
                .map(this::convertToFolderResponse)
                .sorted((f1, f2) -> f2.getUpdatedAt().compareTo(f1.getUpdatedAt())) // 날짜 내림차순 정렬
                .collect(Collectors.toList());
        folderResponse.setChildrenFolders(children);
        return folderResponse;
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
                parentFolder = folderRepository.findById(folderCreateRequest.getParentId())
                        .orElseThrow(() -> new NotFoundException(Folder.class, folderCreateRequest.getParentId()));
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
            newParentFolder = folderRepository.findById(folderUpdateRequest.getParentId())
                    .orElseThrow(() -> new NotFoundException(Folder.class, folderUpdateRequest.getParentId()));
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
