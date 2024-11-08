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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FolderServiceImpl implements FolderService{

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
        List<Folder> folders = folderRepository.findAllAccountIdAndIsDeleted(accountId, false);

        Map<Long, FolderResponse> folderMap = folders.stream()
                .sorted((f1, f2) -> f2.getUpdatedAt().compareTo(f1.getUpdatedAt())) // 날짜 내림차순 정렬
                .collect(Collectors.toMap(
                        Folder::getId,
                        FolderResponse::from,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        // 최상위 폴더들을 저장할 리스트
        List<FolderResponse> rootFolders = new ArrayList<>();

        // 폴더 계층 구조를 구성
        for (Folder folder : folders) {
            FolderResponse folderResponse = folderMap.get(folder.getId());
            if (folder.getParentId() == null) {
                // 최상위 폴더인 경우
                rootFolders.add(folderResponse);
            } else {
                // 하위 폴더인 경우, 부모 폴더의 children 리스트에 추가
                FolderResponse parentFolder = folderMap.get(folder.getParentId());
                if (parentFolder != null) {
                    parentFolder.getChildren().add(folderResponse); // children 리스트에 추가
                }
            }
        }
        return rootFolders;
    }

    /*
        Folder 등록
     */
    @Override
    @Transactional
    public FolderResponse createFolder(FolderCreateRequest folderCreateRequest) {
        try {
            Folder newFolder = Folder.builder()
                    .accountId(folderCreateRequest.getAccountId())
                    .parentId(folderCreateRequest.getParentId())
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
        Folder existingFolder = folderRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new NotFoundException(Folder.class, id));

        existingFolder.updateFields(folderUpdateRequest.getName(), folderUpdateRequest.getParentId());

        folderRepository.save(existingFolder);
    }

    /*
        Folder 삭제
     */
    @Override
    @Transactional
    public void deleteFolder(Long id) {
        Folder existingFolder = folderRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new NotFoundException(Folder.class, id));

        existingFolder.deleteFields(false);

        folderRepository.save(existingFolder);
    }
}
